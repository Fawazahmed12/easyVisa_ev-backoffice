import { AttorneySignUpInfoEffects } from './attorney-sign-up-info/attorney-sign-up-info.effects';
import { AuthModuleRequestEffects } from './requests/effects';

export const effects = [
  AttorneySignUpInfoEffects,
  ...AuthModuleRequestEffects,
];
