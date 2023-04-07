import {
  GetTaskQueueCountsSuccess, NotificationsActionsUnion, NotificationsActionTypes,
} from './notifications.actions';
import {DispositionsActionTypes} from '../../../task-queue/ngrx/dispositions/dispositions.actions';

export const initialState = {
  taskQueueCount: null,
  taskQueueNotifications: null,
};

export function reducer(state = initialState, action: NotificationsActionsUnion) {
  switch (action.type) {

    case NotificationsActionTypes.GetTaskQueueCountsSuccess: {

      const taskQueueNotifications = (action as GetTaskQueueCountsSuccess).payload;
      const alertsUnread = taskQueueNotifications.alerts && taskQueueNotifications.alerts.unread ?
        taskQueueNotifications.alerts.unread : null;
      const warningsUnread = taskQueueNotifications.warnings && taskQueueNotifications.warnings.unread ?
        taskQueueNotifications.warnings.unread : null;
      const dispositions = taskQueueNotifications?.dispositionsCount || null;
      const count = alertsUnread + warningsUnread + dispositions;

      return {
        ...state,
        taskQueueCount: count,
        taskQueueNotifications,
      };
    }

    case NotificationsActionTypes.DecreaseAlertsCount: {
      return {
        ...state,
        taskQueueCount: state.taskQueueCount - 1,
        taskQueueNotifications: {
          ...state.taskQueueNotifications,
          alerts: {
            ...state.taskQueueNotifications?.alerts,
            read: state.taskQueueNotifications?.alerts.read + 1,
            unread: state.taskQueueNotifications?.alerts.unread - 1,
          }
        } || null,
      };
    }

    case NotificationsActionTypes.DecreaseWarningsCount: {
      return {
        ...state,
        taskQueueCount: state.taskQueueCount - 1,
        taskQueueNotifications: {
          ...state.taskQueueNotifications,
          warnings: {
            ...state.taskQueueNotifications.warnings,
            read: state.taskQueueNotifications.warnings.read + 1,
            unread: state.taskQueueNotifications.warnings.unread - 1,
          }
        } || null,
      };
    }

    case DispositionsActionTypes.RemoveDisposition: {
      return {
        ...state,
        taskQueueCount: state.taskQueueCount - 1,
        taskQueueNotifications: {
          ...state.taskQueueNotifications,
          dispositionsCount: state.taskQueueNotifications.dispositionsCount - 1,
        } || null,
      };
    }

    default: {
      return state;
    }
  }
}
