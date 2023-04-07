import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler, RequestFailAction, RequestSuccessAction } from '../../../../core/ngrx/utils';

import { TaskQueueModuleRequestService } from '../request.service';
import { map, tap } from 'rxjs/operators';
import { DeletePackagesSuccess } from '../../../../core/ngrx/packages/packages.actions';

export const deleteSelectedTransferredPackagesRequestHandler = createRequestHandler('RemoveSelectedTransferredPackagesRequest');

export function selectedTransferredPackagesDeleteRequestReducer(state, action) {
  return deleteSelectedTransferredPackagesRequestHandler.reducer(state, action);
}

@Injectable()
export class SelectedTransferredPackagesDeleteRequestEffects {

  @Effect()
  selectedPackagesData$: Observable<Action> = deleteSelectedTransferredPackagesRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.deleteSelectedTransferredPackagesRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect()
  deleteSelectedPackagesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(deleteSelectedTransferredPackagesRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<number[]>) => new DeletePackagesSuccess(payload))
  );

  @Effect({ dispatch: false })
  deleteSelectedPackagesFail$: Observable<Action> = this.actions$.pipe(
    ofType(deleteSelectedTransferredPackagesRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({ payload }: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
  ) {
  }
}
