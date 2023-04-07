import { Action } from '@ngrx/store';

import { FeeSchedule } from '../../models/fee-schedule.model';

import { FEE_SCHEDULE } from './fee-schedule.state';

export const FeeScheduleDataActionTypes  = {
  GetFeeScheduleSettings: `[${FEE_SCHEDULE}] Get Fee Details`,
  GetFeeScheduleSettingsSuccess: `[${FEE_SCHEDULE}] Get Fee Details Success`,
  GetFeeScheduleSettingsFailure: `[${FEE_SCHEDULE}] Get Fee Details Failure`,
  PostFeeScheduleSettings: `[${FEE_SCHEDULE}] Post Fee Schedule Settings`,
  PostFeeScheduleSettingsSuccess: `[${FEE_SCHEDULE}] Post Fee Schedule Settings Success`,
  PostFeeScheduleSettingsFailure: `[${FEE_SCHEDULE}] Post Fee Schedule Settings Failure`,
  OpenFeeScheduleFailModal: `[${FEE_SCHEDULE}] Open Fee Schedule Fail Modal`,
};

export class GetFeeScheduleSettings implements Action {
  readonly type = FeeScheduleDataActionTypes.GetFeeScheduleSettings;
}

export class GetFeeScheduleSettingsSuccess implements Action {
  readonly type = FeeScheduleDataActionTypes.GetFeeScheduleSettingsSuccess;

  constructor(public payload: FeeSchedule[]) {}
}

export class GetFeeScheduleSettingsFailure implements Action {
  readonly type = FeeScheduleDataActionTypes.GetFeeScheduleSettingsFailure;

  constructor(public payload: any) {}
}

export class PostFeeScheduleSettings implements Action {
  readonly type = FeeScheduleDataActionTypes.PostFeeScheduleSettings;

  constructor(public payload: FeeSchedule[]) {}
}

export class PostFeeScheduleSettingsSuccess implements Action {
  readonly type = FeeScheduleDataActionTypes.PostFeeScheduleSettingsSuccess;

  constructor(public payload: FeeSchedule[]) {}
}

export class PostFeeScheduleSettingsFailure implements Action {
  readonly type = FeeScheduleDataActionTypes.PostFeeScheduleSettingsFailure;

  constructor(public payload: any) {}
}

export class OpenFeeScheduleFailModal implements Action {
  readonly type = FeeScheduleDataActionTypes.OpenFeeScheduleFailModal;

  constructor(public payload: any) {}
}



export type FeeScheduleDataActionUnion =
  | GetFeeScheduleSettings
  | GetFeeScheduleSettingsSuccess
  | GetFeeScheduleSettingsFailure
  | PostFeeScheduleSettings
  | PostFeeScheduleSettingsSuccess
  | PostFeeScheduleSettingsFailure
  | OpenFeeScheduleFailModal;
