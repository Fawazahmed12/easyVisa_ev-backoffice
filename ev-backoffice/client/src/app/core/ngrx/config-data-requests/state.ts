import { createFeatureSelector, createSelector } from '@ngrx/store';

import { FeeDetails } from '../../models/fee-details.model';
import { GovernmentFee } from '../../models/government-fee.model';

import { RequestState } from '../utils';
import { BenefitCategoryModel } from '../../models/benefits.model';
import { DashboardSettingsModuleRequestState } from '../../../dashboard/settings/ngrx/requests/state';


export const CONFIG_DATA_REQUEST = 'ConfigDataRequest';

export interface ConfigDataRequestState {
  feeDetailsGet?: RequestState<FeeDetails>;
  feeDetailsPost?: RequestState<FeeDetails>;
  governmentFeeGet?: RequestState<GovernmentFee>;
  benefitsGet?: RequestState<BenefitCategoryModel>;
}

export const selectConfigDataRequestsState = createFeatureSelector<ConfigDataRequestState>(CONFIG_DATA_REQUEST);

export const selectFeeDetailsGetRequestState = createSelector(
  selectConfigDataRequestsState,
  (state: ConfigDataRequestState) => state.feeDetailsGet
);

export const selectFeeDetailsPostRequestState = createSelector(
  selectConfigDataRequestsState,
  (state: ConfigDataRequestState) => state.feeDetailsPost
);

export const selectGovernmentFeeGetRequestState = createSelector(
  selectConfigDataRequestsState,
  (state: ConfigDataRequestState) => state.governmentFeeGet
);

export const selectBenefitsGetRequestState = createSelector(
  selectConfigDataRequestsState,
  (state: ConfigDataRequestState) => state.benefitsGet
);

export { feeDetailsGetRequestHandler } from './fee-details-get/state';
export { feeDetailsPostRequestHandler } from './fee-details-post/state';
export { governmentFeeGetRequestHandler } from './government-fee-get/state';
export { benefitsGetRequestHandler } from './benefits-get/state';
