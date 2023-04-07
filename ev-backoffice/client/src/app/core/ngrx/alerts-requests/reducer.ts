import { AlertsRequestState } from './state';
import { alertsGetRequestReducer } from './alerts-get/state';
import { alertsDeleteRequestReducer } from './alerts-delete/state';
import { alertPutRequestReducer } from './alert-put/state';
import { sendAlertPostRequestReducer } from './send-alert-post/state';

export function reducer(state: AlertsRequestState = {}, action): AlertsRequestState {
  return {
    alertsGet: alertsGetRequestReducer(state.alertsGet, action),
    alertsDelete: alertsDeleteRequestReducer(state.alertsDelete, action),
    alertPut: alertPutRequestReducer(state.alertPut, action),
    sendAlertPost: sendAlertPostRequestReducer(state.sendAlertPost, action),
  };
}
