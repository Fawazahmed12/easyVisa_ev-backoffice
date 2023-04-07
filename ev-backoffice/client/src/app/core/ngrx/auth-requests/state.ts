import { createFeatureSelector, createSelector } from '@ngrx/store';

import { RequestState } from '../utils';

import { LoginResponse } from '../../models/login-response.model';

export const AUTH_REQUEST = 'AuthRequest';

export interface AuthRequestState {
  login?: RequestState<LoginResponse>;
  loginInModal?: RequestState<LoginResponse>;
  logout?: RequestState<{}>;
  changePasswordPut?: RequestState<{access_token: string}>;
}

export const selectAuthRequestState = createFeatureSelector(AUTH_REQUEST);

export const selectLoginRequestState = createSelector(
  selectAuthRequestState,
  (state: AuthRequestState) => state.login
);

export const selectLoginInModalRequestState = createSelector(
  selectAuthRequestState,
  (state: AuthRequestState) => state.loginInModal
);

export const selectLogoutRequestState = createSelector(
  selectAuthRequestState,
  (state: AuthRequestState) => state.logout
);

export const selectChangePasswordPutRequestState = createSelector(
  selectAuthRequestState,
  (state: AuthRequestState) => state.changePasswordPut
);

export { loginRequestHandler } from './login/state';
export { loginInModalRequestHandler } from './login-in-modal/state';
export { logoutRequestHandler } from './logout/state';
