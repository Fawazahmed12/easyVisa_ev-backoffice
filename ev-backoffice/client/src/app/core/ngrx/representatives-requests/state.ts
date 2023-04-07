import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Attorney, AttorneyMenu } from '../../models/attorney.model';

import { FeeSchedule } from '../../models/fee-schedule.model';
import { Organization } from '../../models/organization.model';

import { RequestState } from '../utils';


export const REPRESENTATIVES_REQUEST = 'RepresentativesRequest';

export interface RepresentativesRequestState {
  representativesGet?: RequestState<Attorney[]>;
  representativesMenuGet?: RequestState<AttorneyMenu[]>;
  attorneysValidatePost?: RequestState<{representativeId: number; organizations: Organization[]}>;
  attorneyPatch?: RequestState<Attorney>;
  feeScheduleGet?: RequestState<FeeSchedule[]>;
}

export const selectRepresentativesRequestState = createFeatureSelector<RepresentativesRequestState>(REPRESENTATIVES_REQUEST);

export const selectRepresentativesGetRequestState = createSelector(
  selectRepresentativesRequestState,
  (state: RepresentativesRequestState) => state.representativesGet
);

export const selectRepresentativesMenuGetRequestState = createSelector(
  selectRepresentativesRequestState,
  (state: RepresentativesRequestState) => state.representativesMenuGet
);

export const selectAttorneysValidatePostRequestState = createSelector(
  selectRepresentativesRequestState,
  (state: RepresentativesRequestState) => state.attorneysValidatePost
);

export const selectAttorneysPatchRequestState = createSelector(
  selectRepresentativesRequestState,
  (state: RepresentativesRequestState) => state.attorneyPatch
);

export const selectFeeScheduleGetRequestState = createSelector(
  selectRepresentativesRequestState,
  (state: RepresentativesRequestState) => state.feeScheduleGet
);

export { representativesGetRequestHandler } from './representatives-get/state';
export { representativesMenuGetRequestHandler } from './representatives-menu-get/state';
export { attorneysValidatePostRequestHandler } from './attorneys-validate-post/state';
export { feeScheduleGetRequestHandler } from './fee-schedule-get/state';

