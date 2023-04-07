import { createFeatureSelector, createSelector } from '@ngrx/store';

export const AUTH = 'Auth';

export interface AuthState {
  redirectUrl: string;
}

export const selectAuthState = createFeatureSelector<AuthState>(AUTH);

export const selectRedirectUrl = ({redirectUrl}: AuthState) => redirectUrl;

export const getRedirectUrl = createSelector(
  selectAuthState,
  selectRedirectUrl,
);
