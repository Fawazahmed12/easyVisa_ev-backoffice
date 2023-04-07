import { AuthModuleRequestState } from './state';
import { referringUserRequestReducer } from './referring-user/state';
import { signUpInfoGetRequestReducer } from './sign-up-info-get/state';
import { attorneyPostRequestReducer } from './attorney-post/state';
import { forgotUsernamePostRequestReducer } from './forgot-username-post/state';
import { forgotPasswordPostRequestReducer } from './forgot-password-post/state';
import { paymentPostRequestReducer } from './payment-post/state';
import { resetPasswordPostRequestReducer } from './reset-password-post/state';
import { verifyAttorneyPostRequestReducer } from './verify-attorney-post/state';
import { showUsernamePostRequestReducer } from './show-username-post/state';
import { representativeTypePostRequestReducer } from './representative-type-post/state';
import { completePaymentPostRequestReducer } from './complete-payment-post/state';
import { userPostRequestReducer } from './user-post/state';
import { addReferralRequestReducer } from './add-referral-post/state';

export function reducer(state: AuthModuleRequestState = {}, action): AuthModuleRequestState {
  return {
    referringUserGet: referringUserRequestReducer(state.referringUserGet, action),
    representativeTypePost: representativeTypePostRequestReducer(state.representativeTypePost, action),
    signUpInfoGet: signUpInfoGetRequestReducer(state.signUpInfoGet, action),
    attorneyPost: attorneyPostRequestReducer(state.attorneyPost, action),
    completePaymentPost: completePaymentPostRequestReducer(state.completePaymentPost, action),
    forgotUsernamePost: forgotUsernamePostRequestReducer(state.forgotUsernamePost, action),
    forgotPasswordPost: forgotPasswordPostRequestReducer(state.forgotPasswordPost, action),
    paymentPost: paymentPostRequestReducer(state.paymentPost, action),
    resetPasswordPost: resetPasswordPostRequestReducer(state.resetPasswordPost, action),
    verifyAttorneyPost: verifyAttorneyPostRequestReducer(state.verifyAttorneyPost, action),
    showUsernamePost: showUsernamePostRequestReducer(state.showUsernamePost, action),
    userPost: userPostRequestReducer(state.userPost, action),
    addReferralPost: addReferralRequestReducer(state.userPost, action),
  };
}
