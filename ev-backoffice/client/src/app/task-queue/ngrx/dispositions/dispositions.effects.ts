import { Injectable } from '@angular/core';

import { EMPTY, Observable } from 'rxjs';
import { filter, map, pluck, switchMap, withLatestFrom } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';

import * as FileSaver from 'file-saver';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';

import { Disposition } from '../../models/dispositions.model';

import { dispositionsGetRequestHandler } from '../requests/dispositions-get/state';
import { dispositionDataGetRequestHandler } from '../requests/disposition-data-get/state';
import { dispositionPutRequestHandler } from '../requests/disposition-put/state';
import { getActiveDispositionData, getActiveDispositionId, getDispositions, State } from '../state';

import {
  ApproveActiveDisposition,
  DispositionsActionTypes,
  GetDispositionData,
  GetDispositionDataFailure,
  GetDispositionDataSuccess,
  GetDispositions,
  GetDispositionsFailure,
  GetDispositionsSuccess,
  PutDisposition,
  PutDispositionFailure,
  PutDispositionSuccess,
  RejectDisposition,
  RemoveDisposition,
  ResetActiveDisposition,
  ResetDispositions,
  SetActiveDisposition,
  SetNextActiveDispositionId,
} from './dispositions.actions';
import { HttpResponse } from '@angular/common/http';
import { ModalService } from '../../../core/services';
import { DocumentImageFileType } from '../../../documents/models/documents.model';

@Injectable()
export class DispositionsEffects {

  @Effect()
  getDispositions$: Observable<Action> = this.actions$.pipe(
    ofType(DispositionsActionTypes.GetDispositions),
    map(({ payload }: GetDispositions) => dispositionsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getDispositionsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(dispositionsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<HttpResponse<Disposition[]>>) => new GetDispositionsSuccess(payload))
  );

  @Effect()
  getDispositionsFail$: Observable<Action> = this.actions$.pipe(
    ofType(dispositionsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestSuccessAction<any>) => new GetDispositionsFailure(payload))
  );

  @Effect()
  resetActiveDisposition$: Observable<Action> = this.actions$.pipe(
    ofType(DispositionsActionTypes.GetDispositionsFailure),
    map(() => new ResetDispositions())
  );

  @Effect()
  setActiveDisposition$: Observable<Action> = this.actions$.pipe(
    ofType(DispositionsActionTypes.SetActiveDisposition),
    map(({ payload }: SetActiveDisposition) => new GetDispositionData(payload))
  );

  @Effect()
  getDispositionData$: Observable<Action> = this.actions$.pipe(
    ofType(DispositionsActionTypes.GetDispositionData),
    filter(({ payload }: GetDispositions) => !!payload),
    map(({ payload }: GetDispositionData) => dispositionDataGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getDispositionDataSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(dispositionDataGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new GetDispositionDataSuccess(payload))
  );

  @Effect()
  getDispositionDataFail$: Observable<Action> = this.actions$.pipe(
    ofType(dispositionDataGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetDispositionDataFailure(payload))
  );

  @Effect({ dispatch: false })
  getDispositionDataFailureShowModal$: Observable<Action> = this.actions$.pipe(
    ofType(DispositionsActionTypes.GetDispositionDataFailure),
    switchMap(({ payload }: RequestFailAction<any>) => {
        if (payload.status == 404) {
          return this.modalService.showErrorModal('TEMPLATE.TASK_QUEUE.DISPOSITIONS.FILE_NOT_FOUND')
            .pipe(switchMap((data) => {
              this.modalService.closeAllModals();
              return EMPTY;
            }));
        }
        return EMPTY;
      }
    )
  );

  @Effect()
  putDisposition$: Observable<Action> = this.actions$.pipe(
    ofType(DispositionsActionTypes.PutDisposition),
    map(({ payload }: PutDisposition) => dispositionPutRequestHandler.requestAction(
      {
        payload: { ...payload },
        id: payload.id
      }))
  );

  @Effect()
  putDispositionSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(dispositionPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    pluck('payload'),
    switchMap((payload: any) => {
      const { actionType, ...disposition } = payload;
      switch (actionType) {
        case DispositionsActionTypes.RejectDisposition:
        case DispositionsActionTypes.ApproveActiveDisposition: {
          return [
            new SetNextActiveDispositionId(),
            new RemoveDisposition(payload),
          ];
        }
        default: {
          return [
            new PutDispositionSuccess(disposition)
          ];
        }
      }
    })
  );


  @Effect()
  putDispositionFail$: Observable<Action> = this.actions$.pipe(
    ofType(dispositionPutRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestSuccessAction<any>) => new PutDispositionFailure(payload))
  );


  @Effect({ dispatch: false })
  putDispositionFailureShowModal$: Observable<Action> = this.actions$.pipe(
    ofType(DispositionsActionTypes.PutDispositionFailure),
    switchMap(({ payload }: RequestFailAction<any>) => this.modalService.showErrorModal(
      payload.error.errors || [payload.error] || payload.message)
    )
  );

  @Effect({ dispatch: false })
  downloadActiveDispositionData$: Observable<any> = this.actions$.pipe(
    ofType(DispositionsActionTypes.DownloadActiveDispositionData),
    withLatestFrom(this.store.pipe(select(getActiveDispositionData))),
    map(([, data]) => FileSaver.saveAs(data.file, data.fileName))
  );

  @Effect()
  setNextDispositionId$: Observable<Action> = this.actions$.pipe(
    ofType(
      DispositionsActionTypes.SetNextActiveDispositionId,
      DispositionsActionTypes.SetPreviousActiveDispositionId,
    ),
    withLatestFrom(
      this.store.pipe(select(getActiveDispositionId)),
      this.store.pipe(select(getDispositions)),
    ),
    map(([payload, activeId, dispositions]) => {

        const imageFileTypes = Object.keys(DocumentImageFileType);
        const filteredDispositions = dispositions.filter((disposition) => {
          const fileExt = this.getFileExtension(disposition.fileName);
          return imageFileTypes.some(imageFileType => imageFileType.includes(fileExt));
        });
        const dispositionsLength = filteredDispositions.length;

        if (dispositionsLength === 0 || dispositionsLength === 1) {
          return new ResetActiveDisposition();
        }
        const currentIdIndex = filteredDispositions.findIndex((disposition) => disposition.id === activeId);
        const nextIndex = currentIdIndex + 1;
        const previousIndex = currentIdIndex - 1;
        const lastIndex = dispositionsLength - 1;

        switch (payload.type) {
          case DispositionsActionTypes.SetNextActiveDispositionId: {
            const nextId = currentIdIndex !== lastIndex ? filteredDispositions[ nextIndex ].id : filteredDispositions[ 0 ].id;
            return new SetActiveDisposition(nextId);
          }
          case DispositionsActionTypes.SetPreviousActiveDispositionId: {
            const previousId = currentIdIndex !== 0 ? filteredDispositions[ previousIndex ].id : filteredDispositions[ lastIndex ].id;
            return new SetActiveDisposition(previousId);
          }
        }
      }
    )
  );

  @Effect()
  updateDisposition$: Observable<Action> = this.actions$.pipe(
    ofType(
      DispositionsActionTypes.ApproveActiveDisposition,
      DispositionsActionTypes.RejectDisposition,
    ),
    withLatestFrom(this.store.pipe(select(getActiveDispositionId))),
    map(([data, id]: [ApproveActiveDisposition | RejectDisposition, number]) => dispositionPutRequestHandler.requestAction(
        {
          payload: data.payload,
          actionType: data.type,
          id
        }
      ))
  );

  constructor(
    private actions$: Actions,
    private store: Store<State>,
    private modalService: ModalService,
  ) {
  }

  private getFileExtension(fileName) {
    if (!fileName) {
return '';
}
    return fileName.split('.').pop();
  }
}
