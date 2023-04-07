import { createFeatureSelector } from '@ngrx/store';

import { OrganizationProfile } from '../../profile/edit-preview-profile/models/organization-profile.model';

export const ORGANIZATION = 'Organization';

export interface OrganizationState {
  organization: OrganizationProfile;
}

export const selectOrganizationState = createFeatureSelector<OrganizationState>(ORGANIZATION);

export const selectOrganization = ({organization}: OrganizationState) => organization;
