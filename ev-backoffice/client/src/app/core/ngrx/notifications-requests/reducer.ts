import { NotificationsRequestState } from './state';
import { taskQueueCountsGetRequestReducer } from './task-queue-counts-get/state';

export function reducer(state: NotificationsRequestState = {}, action): NotificationsRequestState {
  return {
    taskQueueCountsGet: taskQueueCountsGetRequestReducer(state.taskQueueCountsGet, action),
  };
}
