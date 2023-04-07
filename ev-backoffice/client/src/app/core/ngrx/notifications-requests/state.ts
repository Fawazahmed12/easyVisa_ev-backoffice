import { createFeatureSelector, createSelector } from '@ngrx/store';

import { TaskQueueCounts } from '../../models/task-queue-counts.model';

import { RequestState } from '../utils';


export const NOTIFICATIONS_REQUEST = 'NotificationsRequest';

export interface NotificationsRequestState {
  taskQueueCountsGet?: RequestState<TaskQueueCounts>;
}

export const selectNotificationsRequestsState = createFeatureSelector<NotificationsRequestState>(NOTIFICATIONS_REQUEST);

export const selectTaskQueueCountsGetRequestState = createSelector(
  selectNotificationsRequestsState,
  (state: NotificationsRequestState) => state.taskQueueCountsGet
);

export { taskQueueCountsGetRequestHandler } from './task-queue-counts-get/state';
