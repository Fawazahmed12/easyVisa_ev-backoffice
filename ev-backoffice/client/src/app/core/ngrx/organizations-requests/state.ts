import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Organization } from '../../models/organization.model';

import { RequestState } from '../utils';


export const ORGANIZATIONS_REQUEST = 'OrganizationsRequest';

export interface OrganizationsRequestState {
  menuOrganizationsGet?: RequestState<Organization[]>;
  affiliatedOrganizationsGet?: RequestState<Organization[]>;
}

export const selectOrganizationsRequestsState = createFeatureSelector<OrganizationsRequestState>(ORGANIZATIONS_REQUEST);

export const selectMenuOrganizationsGetRequestState = createSelector(
  selectOrganizationsRequestsState,
  (state: OrganizationsRequestState) => state.menuOrganizationsGet
);

export const selectAffiliatedOrganizationsGetRequestState = createSelector(
  selectOrganizationsRequestsState,
  (state: OrganizationsRequestState) => state.affiliatedOrganizationsGet
);

export { menuOrganizationsGetRequestHandler } from './menu-organizations-get/state';
export { affiliatedOrganizationsGetRequestHandler } from './affiliated-organizations-get/state';
