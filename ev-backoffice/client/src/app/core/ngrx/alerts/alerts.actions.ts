import { Action } from '@ngrx/store';
import { HttpResponse } from '@angular/common/http';

import { Alert } from '../../../task-queue/models/alert.model';

import { ALERTS } from './alerts.state';

export const AlertsActionTypes = {
  DeleteAlerts: `[${ALERTS}] Delete Alerts`,
  DeleteAlertsSuccess: `[${ALERTS}] Delete Alerts Success`,
  DeleteAlertsFailure: `[${ALERTS}] Delete Alerts Failure`,
  GetAlerts: `[${ALERTS}] Get Alerts`,
  GetAlertsSuccess: `[${ALERTS}] Get Alerts Success`,
  GetAlertsFailure: `[${ALERTS}] Get Alerts Failure`,
  PutAlert: `[${ALERTS}] Put Alert`,
  PutAlertSuccess: `[${ALERTS}] Put Alert Success`,
  PutAlertFailure: `[${ALERTS}] Put Alert Failure`,
  SetActiveAlert: `[${ALERTS}] Set Active Alert`,
  PostAlert: `[${ALERTS}] Post Alert`,
  PostAlertSuccess: `[${ALERTS}] Post Alert Success`,
  PostAlertFailure: `[${ALERTS}] Post Alert Failure`,
};

export class DeleteAlerts implements Action {
  readonly type = AlertsActionTypes.DeleteAlerts;

  constructor(public payload) {
  }
}

export class DeleteAlertsSuccess implements Action {
  readonly type = AlertsActionTypes.DeleteAlertsSuccess;

  constructor(public payload: number[]) {
  }
}

export class DeleteAlertsFailure implements Action {
  readonly type = AlertsActionTypes.DeleteAlertsFailure;

  constructor(public payload?: any) {
  }
}

export class GetAlerts implements Action {
  readonly type = AlertsActionTypes.GetAlerts;

  constructor(public payload?: {sort: string; order: string}) {
  }
}

export class GetAlertsSuccess implements Action {
  readonly type = AlertsActionTypes.GetAlertsSuccess;

  constructor(public payload: { body: Alert[]; xTotalCount: string }) {
  }
}

export class GetAlertsFailure implements Action {
  readonly type = AlertsActionTypes.GetAlertsFailure;

  constructor(public payload?: any) {
  }
}

export class PutAlert implements Action {
  readonly type = AlertsActionTypes.PutAlert;

  constructor(public payload: Alert) {
  }
}

export class PutAlertSuccess implements Action {
  readonly type = AlertsActionTypes.PutAlertSuccess;

  constructor(public payload: Alert) {
  }
}

export class PutAlertFailure implements Action {
  readonly type = AlertsActionTypes.PutAlertFailure;

  constructor(public payload?: any) {
  }
}

export class PostAlert implements Action {
  readonly type = AlertsActionTypes.PostAlert;

  constructor(public payload: Alert) {
  }
}

export class PostAlertSuccess implements Action {
  readonly type = AlertsActionTypes.PostAlertSuccess;

  constructor(public payload: Alert) {
  }
}

export class PostAlertFailure implements Action {
  readonly type = AlertsActionTypes.PostAlertFailure;

  constructor(public payload?: any) {
  }
}

export class SetActiveAlert implements Action {
  readonly type = AlertsActionTypes.SetActiveAlert;

  constructor(public payload: number) {
  }
}

export type AlertsActionsUnion =
  | DeleteAlerts
  | DeleteAlertsSuccess
  | DeleteAlertsFailure
  | GetAlerts
  | GetAlertsSuccess
  | GetAlertsFailure
  | PutAlert
  | PutAlertSuccess
  | PutAlertFailure
  | PostAlert
  | PostAlertSuccess
  | PostAlertFailure
  | SetActiveAlert;
