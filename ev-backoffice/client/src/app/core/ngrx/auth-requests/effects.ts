import { LoginRequestEffects } from './login/state';
import { LoginInModalRequestEffects } from './login-in-modal/state';
import { LogoutRequestEffects } from './logout/state';
import { ChangePasswordPutRequestEffects } from './change-password/state';

export const AuthRequestEffects = [
  LoginRequestEffects,
  LoginInModalRequestEffects,
  LogoutRequestEffects,
  ChangePasswordPutRequestEffects,
];
