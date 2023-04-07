import { Injectable } from '@angular/core';

import { EMPTY, Observable } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { Alert } from '../../../task-queue/models/alert.model';

import { RequestFailAction, RequestSuccessAction } from '../utils';

import { alertsGetRequestHandler } from '../alerts-requests/alerts-get/state';
import { alertPutRequestHandler } from '../alerts-requests/alert-put/state';
import { alertsDeleteRequestHandler } from '../alerts-requests/alerts-delete/state';

import {
  AlertsActionTypes,
  DeleteAlerts, DeleteAlertsFailure,
  DeleteAlertsSuccess,
  GetAlerts,
  GetAlertsSuccess, PostAlert, PostAlertFailure, PostAlertSuccess,
  PutAlert,
  PutAlertSuccess
} from './alerts.actions';
import { sendAlertPostRequestHandler } from '../alerts-requests/send-alert-post/state';
import { OkButton } from '../../modals/confirm-modal/confirm-modal.component';
import { ModalService } from '../../services';

@Injectable()
export class AlertsEffects {

  @Effect()
  deleteAlerts$: Observable<Action> = this.actions$.pipe(
    ofType(AlertsActionTypes.DeleteAlerts),
    map(({payload}: DeleteAlerts) => alertsDeleteRequestHandler.requestAction(payload))
  );

  @Effect()
  deleteAlertsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(alertsDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<any>) => new DeleteAlertsSuccess(payload))
  );

  @Effect()
  getAlertsAfterDelete$: Observable<Action> = this.actions$.pipe(
    ofType(AlertsActionTypes.DeleteAlertsSuccess),
    map(({payload}: DeleteAlerts) => new GetAlerts(payload.params))
  );

  @Effect({dispatch: false})
  deleteAlertsFail$: Observable<Action> = this.actions$.pipe(
    ofType(alertsDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestSuccessAction<number[]>) => new DeleteAlertsFailure(payload))
  );

  @Effect()
  getAlerts$: Observable<Action> = this.actions$.pipe(
    ofType(AlertsActionTypes.GetAlerts),
    map(({payload}: GetAlerts) => alertsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getAlertsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(alertsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Alert[]>) => new GetAlertsSuccess(payload))
  );

  @Effect({dispatch: false})
  getAlertsFail$: Observable<Action> = this.actions$.pipe(
    ofType(alertsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  putAlert$: Observable<Action> = this.actions$.pipe(
    ofType(AlertsActionTypes.PutAlert),
    map(({payload}: PutAlert) => alertPutRequestHandler.requestAction(payload))
  );

  @Effect()
  putAlertSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(alertPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Alert>) => new PutAlertSuccess(payload))
  );

  @Effect({dispatch: false})
  putAlertFail$: Observable<Action> = this.actions$.pipe(
    ofType(alertPutRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  postAlert$: Observable<Action> = this.actions$.pipe(
    ofType(AlertsActionTypes.PostAlert),
    map(({payload}: PostAlert) => sendAlertPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postAlertSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(sendAlertPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Alert>) => new PostAlertSuccess(payload))
  );

  @Effect({dispatch: false})
  postAlertSuccessModal$: Observable<any> = this.actions$.pipe(
    ofType(AlertsActionTypes.PostAlertSuccess),
    switchMap(() => this.modalService.openConfirmModal({
        header: 'FORM.ALERTS.ALERT_CREATED',
        body: 'FORM.ALERTS.ALERT_CREATED_TEXT',
        centered: true,
        buttons: [OkButton],
      }).pipe(
        catchError(() => EMPTY)
      ))
  );

  @Effect()
  postAlertFail$: Observable<Action> = this.actions$.pipe(
    ofType(sendAlertPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PostAlertFailure(payload)),
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
