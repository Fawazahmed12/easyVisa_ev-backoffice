import { Action } from '@ngrx/store';

import { NotificationSettings } from '../../models/notification-settings.model';
import { NotificationTypes } from '../../models/notyfication-types.model';

import { ATTORNEY_NOTIFICATIONS } from './attorney-notifications.state';

export const AttorneyNotificationsActionTypes = {
  GetNotificationsConfig: `[${ATTORNEY_NOTIFICATIONS}] Get Notifications Config`,
  GetNotificationsConfigSuccess: `[${ATTORNEY_NOTIFICATIONS}] Get Notifications Config Success`,
  GetNotificationsConfigFailure: `[${ATTORNEY_NOTIFICATIONS}] Get Notifications Config Failure`,
  PutNotificationsConfig: `[${ATTORNEY_NOTIFICATIONS}] Put Notifications Config`,
  PutNotificationsConfigSuccess: `[${ATTORNEY_NOTIFICATIONS}] Put Notifications Config Success`,
  PutNotificationsConfigFailure: `[${ATTORNEY_NOTIFICATIONS}] Put Notifications Config Failure`,
  GetNotificationTypes: `[${ATTORNEY_NOTIFICATIONS}] Get Notification Types`,
  GetNotificationTypesSuccess: `[${ATTORNEY_NOTIFICATIONS}] Get Notification Types Success`,
  GetNotificationTypesFailure: `[${ATTORNEY_NOTIFICATIONS}] Get Notification Types Failure`,
  SetActiveDeadlineReminder: `[${ATTORNEY_NOTIFICATIONS}] Set Active Deadline Reminder`,
};


export class GetNotificationsConfig implements Action {
  readonly type = AttorneyNotificationsActionTypes.GetNotificationsConfig;

  constructor(public payload: number) {
  }
}

export class GetNotificationsConfigSuccess implements Action {
  readonly type = AttorneyNotificationsActionTypes.GetNotificationsConfigSuccess;

  constructor(public payload: NotificationSettings) {
  }
}

export class GetNotificationsConfigFailure implements Action {
  readonly type = AttorneyNotificationsActionTypes.GetNotificationsConfigFailure;

  constructor(public payload?: any) {
  }
}

export class PutNotificationsConfig implements Action {
  readonly type = AttorneyNotificationsActionTypes.PutNotificationsConfig;
  constructor(public payload: {config: NotificationSettings; id: number,activeOrganizationId:string}) {
  }
}

export class PutNotificationsConfigSuccess implements Action {
  readonly type = AttorneyNotificationsActionTypes.PutNotificationsConfigSuccess;

  constructor(public payload: NotificationSettings) {
  }
}

export class PutNotificationsConfigFailure implements Action {
  readonly type = AttorneyNotificationsActionTypes.PutNotificationsConfigFailure;

  constructor(public payload?: any) {
  }
}

export class GetNotificationTypes implements Action {
  readonly type = AttorneyNotificationsActionTypes.GetNotificationTypes;
}

export class GetNotificationTypesSuccess implements Action {
  readonly type = AttorneyNotificationsActionTypes.GetNotificationTypesSuccess;

  constructor(public payload: NotificationTypes) {
  }
}

export class GetNotificationTypesFailure implements Action {
  readonly type = AttorneyNotificationsActionTypes.GetNotificationTypesFailure;

  constructor(public payload?: any) {
  }
}

export class SetActiveDeadlineReminder implements Action {
  readonly type = AttorneyNotificationsActionTypes.SetActiveDeadlineReminder;

  constructor(public payload: string) {
  }
}


export type AttorneyNotificationsActionsUnion =
  | GetNotificationsConfig
  | GetNotificationsConfigSuccess
  | GetNotificationsConfigFailure
  | PutNotificationsConfig
  | PutNotificationsConfigSuccess
  | PutNotificationsConfigFailure
  | GetNotificationTypes
  | GetNotificationTypesSuccess
  | SetActiveDeadlineReminder
  | GetNotificationTypesFailure;
