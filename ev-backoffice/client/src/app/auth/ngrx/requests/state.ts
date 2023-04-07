import { RequestState } from '../../../core/ngrx/utils';

import { Attorney } from '../../../core/models/attorney.model';
import { ReferringUserModel } from '../../models/referring-user.model';
import { ResetPasswordModel } from '../../models/reset-password.model';
import { Profile } from '../../../core/models/profile.model';
import { LoginResponse } from '../../../core/models/login-response.model';

import { completePaymentPostRequestHandler } from './complete-payment-post/state';
import { forgotUsernamePostRequestHandler } from './forgot-username-post/state';
import { forgotPasswordPostRequestHandler } from './forgot-password-post/state';
import { paymentPostRequestHandler } from './payment-post/state';
import { referringUserGetRequestHandler } from './referring-user/state';
import { representativeTypePostRequestHandler } from './representative-type-post/state';
import { resetPasswordPostRequestHandler } from './reset-password-post/state';
import { showUsernamePostRequestHandler } from './show-username-post/state';
import { addReferralPostRequestHandler } from './add-referral-post/state';


export const AUTH_MODULE_REQUESTS = 'AuthModuleRequests';

export interface AuthModuleRequestState {
  referringUserGet?: RequestState<ReferringUserModel>;
  representativeTypePost?: RequestState<{message: string}>;
  signUpInfoGet?: RequestState<Profile>;
  attorneyPost?: RequestState<Attorney>;
  completePaymentPost?: RequestState<Attorney>;
  forgotUsernamePost?: RequestState<string>;
  forgotPasswordPost?: RequestState<string>;
  paymentPost?: RequestState<{token: string}>;
  resetPasswordPost?: RequestState<ResetPasswordModel>;
  verifyAttorneyPost?: RequestState<string>;
  showUsernamePost?: RequestState<{ token: string }>;
  userPost?: RequestState<LoginResponse>;
  addReferralPost?: RequestState<{email: string}>;
}

export const selectAuthModuleRequestsState = (state) => state[AUTH_MODULE_REQUESTS];

export const selectReferringUserGetState = (state: AuthModuleRequestState) => state.referringUserGet;
export const selectRepresentativeTypePostState = (state: AuthModuleRequestState) => state.representativeTypePost;
export const selectSignUpInfoGetState = (state: AuthModuleRequestState) => state.signUpInfoGet;
export const selectAttorneyPostState = (state: AuthModuleRequestState) => state.attorneyPost;
export const selectCompletePaymentPostState = (state: AuthModuleRequestState) => state.completePaymentPost;
export const selectForgotUsernamePostState = (state: AuthModuleRequestState) => state.forgotUsernamePost;
export const selectForgotPasswordPostState = (state: AuthModuleRequestState) => state.forgotPasswordPost;
export const selectPaymentPostState = (state: AuthModuleRequestState) => state.paymentPost;
export const selectResetPasswordPostState = (state: AuthModuleRequestState) => state.resetPasswordPost;
export const selectVerifyAttorneyPostState = (state: AuthModuleRequestState) => state.verifyAttorneyPost;
export const selectShowUsernamePostState = (state: AuthModuleRequestState) => state.showUsernamePost;
export const selectUserPostState = (state: AuthModuleRequestState) => state.userPost;
export const selectAddReferralPostState = (state: AuthModuleRequestState) => state.addReferralPost;

export { referringUserGetRequestHandler } from './referring-user/state';
export { representativeTypePostRequestHandler } from './representative-type-post/state';
export { signUpInfoGetRequestHandler } from './sign-up-info-get/state';
export { attorneyPostRequestHandler } from './attorney-post/state';
export { completePaymentPostRequestHandler } from './complete-payment-post/state';
export { forgotUsernamePostRequestHandler } from './forgot-username-post/state';
export { forgotPasswordPostRequestHandler } from './forgot-password-post/state';
export { paymentPostRequestHandler } from './payment-post/state';
export { resetPasswordPostRequestHandler } from './reset-password-post/state';
export { verifyAttorneyPostRequestHandler } from './verify-attorney-post/state';
export { showUsernamePostRequestHandler } from './show-username-post/state';
export { userPostRequestHandler } from './user-post/state';
export { addReferralPostRequestHandler } from './add-referral-post/state';
