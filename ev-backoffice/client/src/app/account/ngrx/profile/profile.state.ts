import { createFeatureSelector } from '@ngrx/store';

import { Profile } from '../../../core/models/profile.model';
import { AttorneyProfile } from '../../profile/edit-preview-profile/models/attorney-profile.model';
import { EmployeeProfile } from '../../profile/edit-preview-profile/models/employee-profile.model';

export const PROFILE = 'Profile';

export interface ProfileState {
  profile: AttorneyProfile & EmployeeProfile & Profile;
}

export const selectProfileState = createFeatureSelector<ProfileState>(PROFILE);

export const selectProfile = ({profile}: ProfileState) => profile;
