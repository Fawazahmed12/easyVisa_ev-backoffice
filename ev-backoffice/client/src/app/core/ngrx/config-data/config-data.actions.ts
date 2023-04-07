import { Action } from '@ngrx/store';

import { CONFIG_DATA } from './config-data.state';

import { FeeDetails } from '../../models/fee-details.model';
import { GovernmentFee } from '../../models/government-fee.model';

export const ConfigDataActionTypes  = {
  GetFeeDetails: `[${CONFIG_DATA}] Get Fee Details`,
  GetFeeDetailsSuccess: `[${CONFIG_DATA}] Get Fee Details Success`,
  GetFeeDetailsFailure: `[${CONFIG_DATA}] Get Fee Details Failure`,
  PostFeeDetails: `[${CONFIG_DATA}] Post Fee Details`,
  PostFeeDetailsSuccess: `[${CONFIG_DATA}] Post Fee Details Success`,
  PostFeeDetailsFailure: `[${CONFIG_DATA}] Post Fee Details Failure`,
  GetGovernmentFee: `[${CONFIG_DATA}] Get Government Fee`,
  GetGovernmentFeeSuccess: `[${CONFIG_DATA}] Get Government Fee Success`,
  GetGovernmentFeeFailure: `[${CONFIG_DATA}] Get Government Fee Failure`,
  SetFeeDetails: `[${CONFIG_DATA}] Set Fee Details`,
  SetGovernmentFee: `[${CONFIG_DATA}] Set Government Fee`,
  GetBenefitCategories: `[${CONFIG_DATA}] Get Benefit Categories`,
  GetBenefitCategoriesSuccess: `[${CONFIG_DATA}] Get Benefit Categories Success`,
  GetBenefitCategoriesFailure: `[${CONFIG_DATA}] Get Benefit Categories Failure`,
};

export class GetFeeDetails implements Action {
  readonly type = ConfigDataActionTypes.GetFeeDetails;
}

export class GetFeeDetailsSuccess implements Action {
  readonly type = ConfigDataActionTypes.GetFeeDetailsSuccess;

  constructor(public payload: FeeDetails) {}
}

export class GetFeeDetailsFailure implements Action {
  readonly type = ConfigDataActionTypes.GetFeeDetailsFailure;

  constructor(public payload: any) {}
}

export class PostFeeDetails implements Action {
  readonly type = ConfigDataActionTypes.PostFeeDetails;
  constructor(public payload: FeeDetails) {
  }
}

export class PostFeeDetailsSuccess implements Action {
  readonly type = ConfigDataActionTypes.PostFeeDetailsSuccess;

  constructor(public payload: FeeDetails) {}
}

export class PostFeeDetailsFailure implements Action {
  readonly type = ConfigDataActionTypes.PostFeeDetailsFailure;

  constructor(public payload: any) {}
}

export class GetGovernmentFee implements Action {
  readonly type = ConfigDataActionTypes.GetGovernmentFee;
}

export class GetGovernmentFeeSuccess implements Action {
  readonly type = ConfigDataActionTypes.GetGovernmentFeeSuccess;

  constructor(public payload: GovernmentFee) {}
}

export class GetGovernmentFeeFailure implements Action {
  readonly type = ConfigDataActionTypes.GetGovernmentFeeFailure;

  constructor(public payload: any) {}
}


export class SetFeeDetails implements Action {
  readonly type = ConfigDataActionTypes.SetFeeDetails;

  constructor(public payload: FeeDetails) {}
}

export class SetGovernmentFee implements Action {
  readonly type = ConfigDataActionTypes.SetGovernmentFee;

  constructor(public payload: GovernmentFee) {}
}

export class GetBenefitCategories implements Action {
  readonly type = ConfigDataActionTypes.GetBenefitCategories;
}

export class GetBenefitCategoriesSuccess implements Action {
  readonly type = ConfigDataActionTypes.GetBenefitCategoriesSuccess;

  constructor(public payload: FeeDetails) {}
}

export class GetBenefitCategoriesFailure implements Action {
  readonly type = ConfigDataActionTypes.GetBenefitCategoriesFailure;

  constructor(public payload: any) {}
}

export type ConfigDataActionsUnion =
  | GetFeeDetails
  | GetFeeDetailsSuccess
  | GetFeeDetailsFailure
  | GetGovernmentFee
  | GetGovernmentFeeSuccess
  | GetGovernmentFeeFailure
  | SetFeeDetails
  | SetGovernmentFee;
