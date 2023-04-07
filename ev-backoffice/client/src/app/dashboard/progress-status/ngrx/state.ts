import { createFeatureSelector, createSelector } from '@ngrx/store';

import {
  PROGRESS_STATUSES,
  ProgressStatusesState, selectDocumentProgress,
  selectProgressStatusesState,
  selectQuestionnaireProgress
} from './progress-statuses/progress-statuses.state';
import {
  PROGRESS_STATUSES_MODULE_REQUESTS,
  ProgressStatusesModuleRequestState, selectDocumentProgressGetState,
  selectProgressStatusesModuleRequestsState,
  selectQuestionnaireProgressGetState,
} from './requests/state';

export const PROGRESS_STATUSES_MODULE_STATE = 'ProgressStatusesModuleState';

export interface State {
  [PROGRESS_STATUSES_MODULE_REQUESTS]: ProgressStatusesModuleRequestState;
  [PROGRESS_STATUSES]: ProgressStatusesState;
}

export const selectProgressStatusModuleState = createFeatureSelector<State>(PROGRESS_STATUSES_MODULE_STATE);

export const getProgressStatusModuleRequestState = createSelector(
  selectProgressStatusModuleState,
  selectProgressStatusesModuleRequestsState,
);

export const getQuestionnaireProgressGetRequestState = createSelector(
  getProgressStatusModuleRequestState,
  selectQuestionnaireProgressGetState,
);

export const getDocumentProgressGetRequestState = createSelector(
  getProgressStatusModuleRequestState,
  selectDocumentProgressGetState,
);

export const getProgressStatusesState = createSelector(
  selectProgressStatusModuleState,
  selectProgressStatusesState,
);

export const getQuestionnaireProgress = createSelector(
  getProgressStatusesState,
  selectQuestionnaireProgress,
);

export const getDocumentProgress = createSelector(
  getProgressStatusesState,
  selectDocumentProgress,
);
