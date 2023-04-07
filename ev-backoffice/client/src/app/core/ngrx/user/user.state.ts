import { EntityState } from '@ngrx/entity';
import { createFeatureSelector, createSelector } from '@ngrx/store';

import { User } from '../../models/user.model';
import { getCurrentRepresentativeId } from '../representatives/representatives.state';
import { Role } from '../../models/role.enum';

export const USER = 'User';

export interface UserState extends EntityState<User> {
  currentUserId: string;
  currentUserToken: string;
  loggedIn: boolean;
  registrationRepresentativeType: string;
  findUserId: number;
}

export const selectUserState = createFeatureSelector<UserState>(USER);

export const getCurrentUser = createSelector(
  selectUserState,
  (state: UserState) => state.entities[state.currentUserId] || null
);

export const getCurrentUserEasyVisaId = createSelector(
  selectUserState,
  (state: UserState) => state.entities[state.currentUserId]?.profile?.easyVisaId || null
);

export const getLoggedIn = createSelector(
  selectUserState,
  (state: UserState) => state.loggedIn,
);

export const getCurrentUserToken = createSelector(
  selectUserState,
  (state: UserState) => state.currentUserToken,
);

export const getFindUserId = createSelector(
  selectUserState,
  (state: UserState) => state.findUserId,
);

export const getRegistrationRepresentativeType = createSelector(
  selectUserState,
  (state: UserState) => state.registrationRepresentativeType,
);

export const getCurrentUserActiveMembership = createSelector(
  selectUserState,
  (state: UserState) => state.entities[state.currentUserId] && state.entities[state.currentUserId].activeMembership,
);

export const getCurrentUserPaidStatus = createSelector(
  selectUserState,
  (state: UserState) => state.entities[state.currentUserId] && state.entities[state.currentUserId].paid,
);

export const getCurrentUserRoles = createSelector(
  selectUserState,
  (state: UserState) => state.entities[state.currentUserId] && state.entities[state.currentUserId].roles,
);

export const getIsCurrentRepresentativeMe = createSelector(
  getCurrentUser,
  getCurrentRepresentativeId,
  (currentUser, currentRepresentativeId) => currentUser && currentUser.profile.id === currentRepresentativeId
);

export const getIsEvRole = createSelector(
  getCurrentUser,
  (user) => user.roles?.includes(Role.ROLE_EV)
);


