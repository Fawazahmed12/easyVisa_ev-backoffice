import { createFeatureSelector, createSelector } from '@ngrx/store';

import {
  AUTH_MODULE_REQUESTS,
  AuthModuleRequestState, selectAddReferralPostState,
  selectAttorneyPostState,
  selectAuthModuleRequestsState,
  selectCompletePaymentPostState,
  selectForgotPasswordPostState,
  selectForgotUsernamePostState,
  selectPaymentPostState,
  selectReferringUserGetState,
  selectRepresentativeTypePostState,
  selectResetPasswordPostState,
  selectShowUsernamePostState,
  selectSignUpInfoGetState,
  selectUserPostState,
  selectVerifyAttorneyPostState,
} from './requests/state';

import {
  selectReferringUserName,
  ATTORNEY_SIGN_UP_INFO,
  AttorneySignUpInfoState, selectAttorneySignUpInfoState, selectAttorneySignUpInfo, selectReferralEmail
} from './attorney-sign-up-info/attorney-sign-up-info.state';

export const AUTH_MODULE_STATE = 'AuthModuleState';

export interface State {
  [ATTORNEY_SIGN_UP_INFO]: AttorneySignUpInfoState;
  [AUTH_MODULE_REQUESTS]: AuthModuleRequestState;
}

export const selectAuthModuleState = createFeatureSelector<State>(AUTH_MODULE_STATE);

export const getAuthModuleRequestsState = createSelector(
  selectAuthModuleState,
  selectAuthModuleRequestsState,
);

export const getReferringUserRequestState = createSelector(
  getAuthModuleRequestsState,
  selectReferringUserGetState,
);

export const getSignUpInfoState = createSelector(
  selectAuthModuleState,
  selectAttorneySignUpInfoState,
);

export const getUserPostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectUserPostState,
);

export const getReferringUsername = createSelector(
  getSignUpInfoState,
  selectReferringUserName,
);

export const getAttorneyPostState = createSelector(
  getAuthModuleRequestsState,
  selectAttorneyPostState,
);

export const getRepresentativeTypePostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectRepresentativeTypePostState,
);

export const getAttorneySignUpInfo = createSelector(
  getSignUpInfoState,
  selectAttorneySignUpInfo,
);

export const getReferralEmail = createSelector(
  getSignUpInfoState,
  selectReferralEmail,
);

export const getSignUpInfoRequestState = createSelector(
  getAuthModuleRequestsState,
  selectSignUpInfoGetState,
);

export const getForgotUsernamePostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectForgotUsernamePostState,
);

export const getForgotPasswordPostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectForgotPasswordPostState,
);

export const getResetPasswordPostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectResetPasswordPostState,
);

export const getVerifyAttorneyPostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectVerifyAttorneyPostState,
);

export const getShowUsernamePostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectShowUsernamePostState,
);

export const getPaymentPostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectPaymentPostState,
);

export const getCompletePaymentPostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectCompletePaymentPostState,
);

export const getAddReferralPostRequestState = createSelector(
  getAuthModuleRequestsState,
  selectAddReferralPostState,
);
