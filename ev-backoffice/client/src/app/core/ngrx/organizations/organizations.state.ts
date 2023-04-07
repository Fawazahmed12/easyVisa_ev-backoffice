import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';

import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Organization } from '../../models/organization.model';

export const ORGANIZATIONS = 'Organizations';

export interface OrganizationsState extends EntityState<Organization> {
  activeOrganizationId: string;
  affiliatedOrganizations: Organization[];
}

export const adapter: EntityAdapter<Organization> = createEntityAdapter<Organization>();

export const {selectAll} = adapter.getSelectors();

export const selectOrganizations = selectAll;

export const selectOrganizationsState = createFeatureSelector<OrganizationsState>(ORGANIZATIONS);

export const getOrganizations = createSelector(
  selectOrganizationsState,
  selectOrganizations,
);

export const getActiveOrganization = createSelector(
  selectOrganizationsState,
  (state: OrganizationsState) => state.entities[state.activeOrganizationId] || null
);

export const getActiveOrganizationId = createSelector(
  selectOrganizationsState,
  (state: OrganizationsState) => state.activeOrganizationId,
);

export const getAffiliatedOrganizations = createSelector(
  selectOrganizationsState,
  (state: OrganizationsState) => state.affiliatedOrganizations,
);

export const getCurrentPosition = createSelector(
  selectOrganizationsState,
  (state: OrganizationsState) => state.entities[state.activeOrganizationId] && state.entities[state.activeOrganizationId].position,
);

export const getWithoutOrganizations = createSelector(
  selectOrganizationsState,
  (state: OrganizationsState) => state.ids.length === 0,
);

export const getIsAdmin = createSelector(
  getActiveOrganization,
  (activeOrg: Organization) => !!activeOrg && activeOrg.isAdmin || null
);


