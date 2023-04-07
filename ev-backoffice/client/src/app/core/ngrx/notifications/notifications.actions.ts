import { Action } from '@ngrx/store';

import { TaskQueueCounts } from '../../models/task-queue-counts.model';

import { NOTIFICATIONS } from './notifications.state';

export const NotificationsActionTypes = {
  DecreaseAlertsCount: `[${NOTIFICATIONS}] Decrease Alerts Count`,
  DecreaseWarningsCount: `[${NOTIFICATIONS}] Decrease Warnings Count`,
  GetTaskQueueCounts: `[${NOTIFICATIONS}] Get Task Queue Counts`,
  GetTaskQueueCountsSuccess: `[${NOTIFICATIONS}] Get Task Queue Counts Success`,
  GetTaskQueueCountsFailure: `[${NOTIFICATIONS}] Get Task Queue Counts Failure`,
};

export class DecreaseAlertsCount implements Action {
  readonly type = NotificationsActionTypes.DecreaseAlertsCount;
}

export class DecreaseWarningsCount implements Action {
  readonly type = NotificationsActionTypes.DecreaseWarningsCount;
}

export class GetTaskQueueCounts implements Action {
  readonly type = NotificationsActionTypes.GetTaskQueueCounts;

  constructor(public payload: {representativeId: number; organizationId: number}) {
  }
}

export class GetTaskQueueCountsSuccess implements Action {
  readonly type = NotificationsActionTypes.GetTaskQueueCountsSuccess;

  constructor(public payload: TaskQueueCounts) {
  }
}

export class GetTaskQueueCountsFailure implements Action {
  readonly type = NotificationsActionTypes.GetTaskQueueCountsFailure;

  constructor(public payload: any) {
  }
}

export type NotificationsActionsUnion =
  | DecreaseAlertsCount
  | DecreaseWarningsCount
  | GetTaskQueueCounts
  | GetTaskQueueCountsSuccess
  | GetTaskQueueCountsFailure;
