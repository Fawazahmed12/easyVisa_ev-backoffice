import { AttorneyNotificationsState } from './attorney-notifications.state';
import {
  AttorneyNotificationsActionsUnion,
  AttorneyNotificationsActionTypes,
  GetNotificationsConfigSuccess, GetNotificationTypesSuccess, SetActiveDeadlineReminder
} from './attorney-notifications.actions';

export const initialState: AttorneyNotificationsState = {
  taskQueueSettings: null,
  clientProgressSettings: null,
  notificationTypes: null,
};

export function reducer(state = initialState, action: AttorneyNotificationsActionsUnion) {
  switch (action.type) {

    case AttorneyNotificationsActionTypes.GetNotificationsConfigSuccess:
    case AttorneyNotificationsActionTypes.PutNotificationsConfigSuccess: {
      const notificationsConfig = (action as GetNotificationsConfigSuccess).payload;

      return {
        ...state,
        taskQueueSettings: notificationsConfig.taskQueue,
        clientProgressSettings: notificationsConfig.clientProgress,
      };
    }

    case AttorneyNotificationsActionTypes.GetNotificationTypesSuccess: {

      return {
        ...state,
        notificationTypes: {
          ...(action as GetNotificationTypesSuccess).payload,
        },
      };
    }

    case AttorneyNotificationsActionTypes.SetActiveDeadlineReminder: {

      return {
        ...state,
        activeDeadlineReminderValue: (action as SetActiveDeadlineReminder).payload,
      };
    }

    default: {
      return state;
    }
  }
}
