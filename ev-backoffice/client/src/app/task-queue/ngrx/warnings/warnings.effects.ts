import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';
import { ModalService } from '../../../core/services/modal.service';

import {
  DeleteWarnings,
  DeleteWarningsSuccess,
  GetWarnings,
  GetWarningsSuccess,
  PutWarning,
  PutWarningSuccess, ShowErrorWarningModal,
  WarningsActionTypes,
} from './warnings.actions';
import { Warning } from '../../models/warning.model';
import { warningsGetRequestHandler } from '../requests/warnings-get/state';
import { warningsDeleteRequestHandler } from '../requests/warnings-delete/state';
import { warningPutRequestHandler } from '../requests/warning-put/state';


@Injectable()
export class WarningsEffects {

  @Effect()
  deleteWarnings$: Observable<Action> = this.actions$.pipe(
    ofType(WarningsActionTypes.DeleteWarnings),
    map(({payload}: DeleteWarnings) => warningsDeleteRequestHandler.requestAction(payload))
  );

  @Effect()
  deleteWarningsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(warningsDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<number[]>) => new DeleteWarningsSuccess(payload))
  );

  @Effect()
  deleteWarningsFail$: Observable<Action> = this.actions$.pipe(
    ofType(warningsDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestSuccessAction<any>) => new ShowErrorWarningModal(payload))
  );

  @Effect()
  getWarningsAfterDelete$: Observable<Action> = this.actions$.pipe(
    ofType(WarningsActionTypes.DeleteWarningsSuccess),
    map(({payload}: DeleteWarningsSuccess) => new GetWarnings(payload.params))
  );

  @Effect()
  getWarnings$: Observable<Action> = this.actions$.pipe(
    ofType(WarningsActionTypes.GetWarnings),
    map(({payload}: GetWarnings) => warningsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getWarningsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(warningsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Warning[]>) => new GetWarningsSuccess(payload))
  );

  @Effect({dispatch: false})
  getWarningsFail$: Observable<Action> = this.actions$.pipe(
    ofType(warningsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  putWarning$: Observable<Action> = this.actions$.pipe(
    ofType(WarningsActionTypes.PutWarning),
    map(({payload}: PutWarning) => warningPutRequestHandler.requestAction(payload))
  );

  @Effect()
  putWarningSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(warningPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Warning>) => new PutWarningSuccess(payload))
  );

  @Effect()
  putWarningFail$: Observable<Action> = this.actions$.pipe(
    ofType(warningPutRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new ShowErrorWarningModal(payload))
  );

  @Effect({dispatch: false})
  showWarningsErrorModal$: Observable<Action> = this.actions$.pipe(
    ofType(WarningsActionTypes.ShowErrorWarningModal),
    tap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || [payload.error]))
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }

}
