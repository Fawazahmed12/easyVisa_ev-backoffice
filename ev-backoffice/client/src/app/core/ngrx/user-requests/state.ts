import { createFeatureSelector, createSelector } from '@ngrx/store';

import { User } from '../../models/user.model';

import { RequestState } from '../utils';

import { userGetRequestHandler } from './get/state';

export const USER_REQUEST = 'UserRequest';

export interface UserRequestState {
  get?: RequestState<User>;
  userDelete?: RequestState<number>;
  userIdByEVIdGet?: RequestState<number>;
  changeMembershipPatch?: RequestState<User>;
  convertToAttorneyPost?: RequestState<any>;
}

export const selectUserRequestState = createFeatureSelector(USER_REQUEST);

export const selectUserGetState = createSelector(
  selectUserRequestState,
  (state: UserRequestState) => state.get
);

export const selectUserIdByEVIdGetState = createSelector(
  selectUserRequestState,
  (state: UserRequestState) => state.userIdByEVIdGet
);

export const selectUserDeleteState = createSelector(
  selectUserRequestState,
  (state: UserRequestState) => state.userDelete
);

export const selectChangeMembershipPatchState = createSelector(
  selectUserRequestState,
  (state: UserRequestState) => state.changeMembershipPatch
);

export const selectConvertToAttorneyPostState = createSelector(
  selectUserRequestState,
  (state: UserRequestState) => state.convertToAttorneyPost
);

export { userGetRequestHandler } from './get/state';
export { userDeleteRequestHandler } from './delete-user/state';
export { userIdByEVIdGetRequestHandler } from './get-user-id-by-evid/state';
export { changeMembershipPatchRequestHandler } from './change-membership/state';
export { convertToAttorneyPostRequestHandler } from './convert-to-attorney/state';
