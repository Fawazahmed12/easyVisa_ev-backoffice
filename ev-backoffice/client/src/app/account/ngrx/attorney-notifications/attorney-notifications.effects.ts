import { Injectable } from '@angular/core';

import { EMPTY, Observable } from 'rxjs';
import {catchError, map, switchMap, tap} from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';
import { ModalService } from '../../../core/services';

import { NotificationSettings } from '../../models/notification-settings.model';
import {
  notificationsConfigGetRequestHandler,
  notificationsConfigPutRequestHandler,
  notificationTypesGetRequestHandler
} from '../requests/state';
import {
  AttorneyNotificationsActionTypes, GetNotificationsConfig,
  GetNotificationsConfigFailure,
  GetNotificationsConfigSuccess, GetNotificationTypesFailure,
  GetNotificationTypesSuccess,
  PutNotificationsConfig,
  PutNotificationsConfigFailure,
  PutNotificationsConfigSuccess,
} from './attorney-notifications.actions';
import { NotificationTypes } from '../../models/notyfication-types.model';
import {OkButton} from '../../../core/modals/confirm-modal/confirm-modal.component';


@Injectable()
export class AttorneyNotificationsEffects {

  @Effect()
  getNotificationsConfig$: Observable<Action> = this.actions$.pipe(
    ofType(AttorneyNotificationsActionTypes.GetNotificationsConfig),
    map(({payload}: GetNotificationsConfig) => notificationsConfigGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getNotificationsConfigSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(notificationsConfigGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<NotificationSettings>) => new GetNotificationsConfigSuccess(payload))
  );

  @Effect()
  getNotificationsConfigFail$: Observable<Action> = this.actions$.pipe(
    ofType(notificationsConfigGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestSuccessAction<any>) => new GetNotificationsConfigFailure(payload))
  );

  @Effect()
  putNotificationsConfig$: Observable<Action> = this.actions$.pipe(
    ofType(AttorneyNotificationsActionTypes.PutNotificationsConfig),
    map(({payload}: PutNotificationsConfig) => notificationsConfigPutRequestHandler.requestAction(payload))
  );

  @Effect()
  putNotificationsConfigSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(notificationsConfigPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<NotificationSettings>) => new PutNotificationsConfigSuccess(payload)),
    tap(({payload}: RequestFailAction<any>) => {
      this.modalService.openConfirmModal({
        header: 'TEMPLATE.ACCOUNT.NOTIFICATIONS_REMINDERS.SUCCESS_MODAL.TITLE',
        body: 'TEMPLATE.ACCOUNT.NOTIFICATIONS_REMINDERS.SUCCESS_MODAL.P_1',
        buttons: [OkButton],
        centered: true,
      }
    );
})
  );

  @Effect()
  putNotificationsConfigFail$: Observable<Action> = this.actions$.pipe(
    ofType(notificationsConfigPutRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestSuccessAction<any>) => new PutNotificationsConfigFailure(payload))
  );

  @Effect({dispatch: false})
  openFailModal$: Observable<Action> = this.actions$.pipe(
    ofType(
      AttorneyNotificationsActionTypes.GetNotificationsConfigFailure,
      AttorneyNotificationsActionTypes.PutNotificationsConfigFailure,
    ),
    switchMap(({payload}: RequestFailAction<any>) =>
      this.modalService.showErrorModal(payload.error.errors || [payload.error]).pipe(
        catchError(() => EMPTY))
    )
  );

  @Effect()
  getNotificationsTypes$: Observable<Action> = this.actions$.pipe(
    ofType(AttorneyNotificationsActionTypes.GetNotificationTypes),
    map(() => notificationTypesGetRequestHandler.requestAction())
  );

  @Effect()
  getNotificationsTypesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(notificationTypesGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<NotificationTypes>) => new GetNotificationTypesSuccess(payload))
  );

  @Effect()
  getNotificationsTypesFail$: Observable<Action> = this.actions$.pipe(
    ofType(notificationTypesGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestSuccessAction<any>) => new GetNotificationTypesFailure(payload))
  );


  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
