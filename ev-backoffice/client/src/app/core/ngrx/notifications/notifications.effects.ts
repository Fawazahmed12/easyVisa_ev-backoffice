import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, map, pluck, switchMapTo, tap, withLatestFrom } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { AlertsActionTypes, DeleteAlertsSuccess } from '../alerts/alerts.actions';
import { WarningsActionTypes } from '../../../task-queue/ngrx/warnings/warnings.actions';

import { TaskQueueCounts } from '../../models/task-queue-counts.model';
import { NotificationsService, OrganizationService } from '../../services';
import { Role } from '../../models/role.enum';

import { taskQueueCountsGetRequestHandler } from '../notifications-requests/task-queue-counts-get/state';
import { RequestFailAction, RequestSuccessAction } from '../utils';
import { GetUserSuccess, UserActionTypes } from '../user/user.actions';

import { GetTaskQueueCounts, GetTaskQueueCountsSuccess, NotificationsActionTypes } from './notifications.actions';



@Injectable()
export class NotificationsEffects {

  @Effect()
  getTaskQueueCounts$: Observable<Action> = this.actions$.pipe(
    ofType(NotificationsActionTypes.GetTaskQueueCounts),
    map(({payload}: GetTaskQueueCounts) => taskQueueCountsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getTaskQueueCountsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(taskQueueCountsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<TaskQueueCounts>) => new GetTaskQueueCountsSuccess(payload))
  );

  @Effect({dispatch: false})
  getTaskQueueCountsFail$: Observable<Action> = this.actions$.pipe(
    ofType(taskQueueCountsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  getTaskQueueCountsAfterSelectorsChanges$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.GetUserSuccess),
    map(action => (action as GetUserSuccess).payload),
    pluck('roles'),
    filter(roles => !!roles.some(role => role !== Role.ROLE_USER)),
    switchMapTo(this.organizationService.currentRepIdOrgId$),
    map(([representativeId, organizationId]) => new GetTaskQueueCounts({representativeId, organizationId}))
  );

  @Effect({dispatch: false})
  clearIsShowedPaymentWarningFromLocalStorage$: Observable<Action> = this.actions$.pipe(
    ofType(
      UserActionTypes.Logout,
    ),
    tap(() => this.notificationsService.removeIsShowedPaymentWarning()),
  );

  constructor(
    private actions$: Actions,
    private organizationService: OrganizationService,
    private notificationsService: NotificationsService,
  ) {
  }
}
