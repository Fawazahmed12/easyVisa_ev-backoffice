import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Alert } from '../../../task-queue/models/alert.model';

import { RequestState } from '../utils';

export const ALERTS_REQUEST = 'AlertsRequest';

export interface AlertsRequestState {
  alertsGet?: RequestState<Alert[]>;
  alertPut?: RequestState<Alert>;
  alertsDelete?: RequestState<number[]>;
  sendAlertPost?: RequestState<any>;
}

export const selectAlertRequestState = createFeatureSelector(ALERTS_REQUEST);

export const selectAlertsGetRequestState = createSelector(
  selectAlertRequestState,
  (state: AlertsRequestState) => state.alertsGet
);

export const selectAlertPutRequestState = createSelector(
  selectAlertRequestState,
  (state: AlertsRequestState) => state.alertPut
);

export const selectAlertsDeleteRequestState = createSelector(
  selectAlertRequestState,
  (state: AlertsRequestState) => state.alertsDelete
);

export const selectSendAlertPostRequestState = createSelector(
  selectAlertRequestState,
  (state: AlertsRequestState) => state.sendAlertPost
);

export { alertsGetRequestHandler } from './alerts-get/state';
export { alertPutRequestHandler } from './alert-put/state';
export { alertsDeleteRequestHandler  } from './alerts-delete/state';
export { sendAlertPostRequestHandler  } from './send-alert-post/state';
