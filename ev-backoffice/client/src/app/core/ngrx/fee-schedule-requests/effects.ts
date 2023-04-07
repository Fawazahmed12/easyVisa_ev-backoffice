import { FeeScheduleSettingsGetRequestEffects } from './fee-schedule-settings-get/state';
import { FeeScheduleSettingsPostRequestEffects } from './fee-schedule-settings-post/state';

export const FeeScheduleRequestEffects = [
  FeeScheduleSettingsGetRequestEffects,
  FeeScheduleSettingsPostRequestEffects,
];
