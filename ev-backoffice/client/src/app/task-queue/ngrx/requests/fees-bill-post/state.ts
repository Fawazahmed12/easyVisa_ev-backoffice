import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';
import {map, switchMap, tap} from 'rxjs/operators';

import { OpenPackagesFailModals } from '../../../../core/ngrx/packages/packages.actions';
import {createRequestHandler, RequestFailAction, RequestSuccessAction} from '../../../../core/ngrx/utils';

import { TaskQueueModuleRequestService } from '../request.service';
import {OkButton} from '../../../../core/modals/confirm-modal/confirm-modal.component';
import {ModalService} from '../../../../core/services/modal.service';

export const feesBillPostRequestHandler = createRequestHandler('PostFeesBillRequest');

export function feesBillPostRequestReducer(state, action) {
  return feesBillPostRequestHandler.reducer(state, action);
}

@Injectable()
export class FeesBillPostRequestEffects {

  @Effect()
  feesBill$: Observable<Action> = feesBillPostRequestHandler.effect(
    this.actions$,
    this.taskQueueModuleRequestService.feesBillPostRequest.bind(this.taskQueueModuleRequestService)
  );

  @Effect()
  feesBillFail$: Observable<Action> = this.actions$.pipe(
    ofType(feesBillPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestSuccessAction<any>) => new OpenPackagesFailModals(payload.error.errors))
  );

  @Effect({dispatch: false})
  feesBillSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(feesBillPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    switchMap(() =>
      this.modalService.openConfirmModal({
        header: 'TEMPLATE.TASK_QUEUE.ADDITIONAL_FEES.SENT_SUCCESS_TITLE',
        body: 'TEMPLATE.TASK_QUEUE.ADDITIONAL_FEES.SENT_SUCCESS_P1',


        centered: true,
        buttons: [OkButton],
      })
    )
  );

  constructor(
    private actions$: Actions,
    private taskQueueModuleRequestService: TaskQueueModuleRequestService,
    private modalService: ModalService
  ) {
  }
}
