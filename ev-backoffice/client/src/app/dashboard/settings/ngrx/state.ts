import { createFeatureSelector, createSelector } from '@ngrx/store';

import {
  DASHBOARD_SETTINGS_MODULE_REQUESTS,
  DashboardSettingsModuleRequestState,
  selectBatchJobsGetRequestState,
  selectBatchJobsPatchRequestState,
  selectDashboardSettingsModuleRequestsState,
  selectGovernmentFeePostRequestState,
  selectRankingDataGetRequestState,
  selectRankingDataPutRequestState,
  selectRepresentativesGetRequestState
} from './requests/state';
import {
  selectBatchJob,
  selectRankingsData,
  selectRepresentativesCount,
  selectSettingsState,
  SETTINGS,
  SettingsState
} from './settings/settings.state';

export const DASHBOARD_SETTINGS_MODULE_STATE = 'DashboardSettingsModuleState';

export interface State {
  [DASHBOARD_SETTINGS_MODULE_REQUESTS]: DashboardSettingsModuleRequestState;
  [SETTINGS]: SettingsState;
}

export const selectDashboardSettingsModuleState = createFeatureSelector<State>(DASHBOARD_SETTINGS_MODULE_STATE);

export const getDashboardSettingsModuleRequestsStatee = createSelector(
  selectDashboardSettingsModuleState,
  selectDashboardSettingsModuleRequestsState,
);

export const getGovernmentFeePostRequestState = createSelector(
  getDashboardSettingsModuleRequestsStatee,
  selectGovernmentFeePostRequestState,
);

export const getRankingDataGetRequestState = createSelector(
  getDashboardSettingsModuleRequestsStatee,
  selectRankingDataGetRequestState,
);

export const getRankingDataPutRequestState = createSelector(
  getDashboardSettingsModuleRequestsStatee,
  selectRankingDataPutRequestState,
);


export const getSettingsState = createSelector(
  selectDashboardSettingsModuleState,
  selectSettingsState,
);

export const getRankingData = createSelector(
  getSettingsState,
  selectRankingsData,
);

export const getRepresentativesCount = createSelector(
  getSettingsState,
  selectRepresentativesCount,
);

export const getRepresentativesCountGetRequestState = createSelector(
  getDashboardSettingsModuleRequestsStatee,
  selectRepresentativesGetRequestState,
);

export const getBatchJobs = createSelector(
  getSettingsState,
  selectBatchJob,
);

export const getBatchJobsGetRequestState = createSelector(
  getDashboardSettingsModuleRequestsStatee,
  selectBatchJobsGetRequestState,
);

export const getBatchJobsPatchRequestState = createSelector(
  getDashboardSettingsModuleRequestsStatee,
  selectBatchJobsPatchRequestState,
);
