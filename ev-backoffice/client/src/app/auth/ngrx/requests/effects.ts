import { ReferringUserGetRequestEffects } from './referring-user/state';
import { AttorneyPostRequestEffects } from './attorney-post/state';
import { CompletePaymentPostRequestEffects } from './complete-payment-post/state';
import { ForgotUsernamePostRequestEffects } from './forgot-username-post/state';
import { ForgotPasswordPostRequestEffects } from './forgot-password-post/state';
import { PaymentPostRequestEffects } from './payment-post/state';
import { ResetPasswordPostRequestEffects } from './reset-password-post/state';
import { VerifyAttorneyPostRequestEffects } from './verify-attorney-post/state';
import { ShowUsernamePostRequestEffects } from './show-username-post/state';
import { RepresentativeTypePostRequestEffects } from './representative-type-post/state';
import { SignUpInfoGetRequestEffects } from './sign-up-info-get/state';
import { UserPostRequestEffects } from './user-post/state';
import { AddReferralPostRequestEffects } from './add-referral-post/state';

export const AuthModuleRequestEffects = [
  SignUpInfoGetRequestEffects,
  ReferringUserGetRequestEffects,
  RepresentativeTypePostRequestEffects,
  AttorneyPostRequestEffects,
  CompletePaymentPostRequestEffects,
  ForgotUsernamePostRequestEffects,
  ForgotPasswordPostRequestEffects,
  PaymentPostRequestEffects,
  ResetPasswordPostRequestEffects,
  VerifyAttorneyPostRequestEffects,
  ShowUsernamePostRequestEffects,
  UserPostRequestEffects,
  AddReferralPostRequestEffects,
];
