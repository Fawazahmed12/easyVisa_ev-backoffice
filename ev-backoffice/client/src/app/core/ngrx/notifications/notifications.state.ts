import { createFeatureSelector, createSelector } from '@ngrx/store';

import { TaskQueueCounts } from '../../models/task-queue-counts.model';

export const NOTIFICATIONS = 'Notifications';

export interface NotificationsState {
  taskQueueCount: number;
  taskQueueNotifications: TaskQueueCounts;
}

export const selectNotificationsState = createFeatureSelector<NotificationsState>(NOTIFICATIONS);

export const selectTaskQueueCount = ({taskQueueCount}: NotificationsState) => taskQueueCount;
export const selectTaskQueueNotifications = ({taskQueueNotifications}: NotificationsState) => taskQueueNotifications;

export const getTaskQueueCount = createSelector(
  selectNotificationsState,
  selectTaskQueueCount,
);

export const getTaskQueueNotifications = createSelector(
  selectNotificationsState,
  selectTaskQueueNotifications,
);

