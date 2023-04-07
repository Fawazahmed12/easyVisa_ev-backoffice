import { SignUpSuccessGuardService } from './sign-up-success-guard.service';
import { LoggedInGuardService } from './logged-in-guard.service';

export const GUARD_PROVIDERS = [
  SignUpSuccessGuardService,
  LoggedInGuardService,
];

export * from './sign-up-success-guard.service';
export * from './logged-in-guard.service';
