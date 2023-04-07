import { PermissionsModuleRequestEffects } from './requests/effects';
import { PermissionsEffects } from './permissions/permissions.effects';

export const effects = [
  PermissionsEffects,
  ...PermissionsModuleRequestEffects,
];
