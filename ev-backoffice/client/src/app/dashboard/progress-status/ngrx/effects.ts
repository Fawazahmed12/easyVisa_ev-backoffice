import { ProgressStatusesModuleRequestEffects } from './requests/effects';
import { ProgressStatusesEffects } from './progress-statuses/progress-statuses.effects';

export const effects = [
  ProgressStatusesEffects,
  ...ProgressStatusesModuleRequestEffects,
];
