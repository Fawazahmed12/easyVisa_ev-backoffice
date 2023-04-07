import { DashboardSettingsModuleRequestEffects } from './requests/effects';
import { SettingsEffects } from './settings/settings.effects';

export const effects = [
  SettingsEffects,
  ...DashboardSettingsModuleRequestEffects,
];
