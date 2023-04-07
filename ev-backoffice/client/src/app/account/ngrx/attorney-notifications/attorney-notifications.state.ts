import { createFeatureSelector } from '@ngrx/store';
import { NotificationTypes } from '../../models/notyfication-types.model';

export const ATTORNEY_NOTIFICATIONS = 'AttorneyNotifications';

export interface AttorneyNotificationsState {
  taskQueueSettings: any;
  clientProgressSettings: any;
  notificationTypes: NotificationTypes;
}

export const selectAttorneyNotificationsState = createFeatureSelector<AttorneyNotificationsState>(ATTORNEY_NOTIFICATIONS);

export const selectTaskQueueSettings = ({taskQueueSettings}: AttorneyNotificationsState) => taskQueueSettings;
export const selectClientProgressSettings = ({clientProgressSettings}: AttorneyNotificationsState) => clientProgressSettings;
export const selectNotificationTypes = ({notificationTypes}: AttorneyNotificationsState) => notificationTypes;
