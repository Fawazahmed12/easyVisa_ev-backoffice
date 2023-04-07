import { createFeatureSelector, createSelector } from '@ngrx/store';

import { RequestState } from '../utils';

export const ALERT_HANDLING_REQUEST = 'AlertHandlingRequest';

export interface AlertHandlingRequestState {
  alertReplyPut?: RequestState<{message: string}>;
}

export const selectAlertHandlingRequestState = createFeatureSelector(ALERT_HANDLING_REQUEST);

export const selectAlertReplyRequestState = createSelector(
  selectAlertHandlingRequestState,
  (state: AlertHandlingRequestState) => state.alertReplyPut
);

export { alertReplyPutRequestHandler } from './alert-reply-put/state';

