import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { EMPTY, Observable } from 'rxjs';

import { ModalService } from '../../../services';
import { createRequestHandler, RequestSuccessAction, } from '../../utils';

import { catchError, switchMap } from 'rxjs/operators';
import { AlertsRequestService } from '../../services';

export const sendAlertPostRequestHandler = createRequestHandler('PostSendAlertRequest');

export function sendAlertPostRequestReducer(state, action) {
  return sendAlertPostRequestHandler.reducer(state, action);
}

@Injectable()
export class SendAlertPostRequestEffects {

  @Effect()
  sendAlert$: Observable<Action> = sendAlertPostRequestHandler.effect(
    this.actions$,
    this.alertsRequestService.sendAlertRequest.bind(this.alertsRequestService)
  );

  @Effect({dispatch: false})
  sendAlertFail$: Observable<Action> = this.actions$.pipe(
    ofType(sendAlertPostRequestHandler.ActionTypes.REQUEST_FAIL),
    switchMap(({payload}: RequestSuccessAction<any>) => this.modalService.showErrorModal(
      payload.error.errors || [payload.error]
    ).pipe(
      catchError(() => EMPTY)
    ))
  );

  constructor(
    private actions$: Actions,
    private alertsRequestService: AlertsRequestService,
    private modalService: ModalService,
  ) {
  }
}
