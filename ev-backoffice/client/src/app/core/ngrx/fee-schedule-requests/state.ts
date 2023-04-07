import { createFeatureSelector, createSelector } from '@ngrx/store';

import { FeeSchedule } from '../../models/fee-schedule.model';

import { RequestState } from '../utils';


export const FEE_SCHEDULE_REQUEST = 'FeeScheduleRequest';

export interface FeeScheduleRequestState {
  feeScheduleSettingsGet?: RequestState<FeeSchedule[]>;
  feeScheduleSettingsPost?: RequestState<FeeSchedule[]>;
}

export const selectFeeScheduleRequestsState = createFeatureSelector<FeeScheduleRequestState>(FEE_SCHEDULE_REQUEST);

export const selectFeeScheduleSettingsGetRequestState = createSelector(
  selectFeeScheduleRequestsState,
  (state: FeeScheduleRequestState) => state.feeScheduleSettingsGet
);

export const selectFeeScheduleSettingsPostRequestState = createSelector(
  selectFeeScheduleRequestsState,
  (state: FeeScheduleRequestState) => state.feeScheduleSettingsPost
);

export { feeScheduleSettingsGetRequestHandler } from './fee-schedule-settings-get/state';
