import { createFeatureSelector, createSelector } from '@ngrx/store';

import { FeeSchedule } from '../../models/fee-schedule.model';

export const FEE_SCHEDULE = 'FeeSchedule';

export interface FeeScheduleState {
  feeScheduleSettings: FeeSchedule[];
}

export const selectFeeScheduleState = createFeatureSelector<FeeScheduleState>(FEE_SCHEDULE);

export const selectFeeScheduleSettings = ({feeScheduleSettings}: FeeScheduleState) => feeScheduleSettings;

export const getFeeScheduleSettings = createSelector(
  selectFeeScheduleState,
  selectFeeScheduleSettings,
);
