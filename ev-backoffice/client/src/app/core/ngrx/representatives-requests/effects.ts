import { RepresentativesGetRequestEffects } from './representatives-get/state';
import { AttorneysValidatePostRequestEffects } from './attorneys-validate-post/state';
import { AttorneyPatchRequestEffects } from './attorney-patch/state';
import { RepresentativesMenuGetRequestEffects } from './representatives-menu-get/state';
import { FeeScheduleGetRequestEffects } from './fee-schedule-get/state';

export const RepresentativesRequestEffects = [
  RepresentativesGetRequestEffects,
  RepresentativesMenuGetRequestEffects,
  AttorneysValidatePostRequestEffects,
  AttorneyPatchRequestEffects,
  FeeScheduleGetRequestEffects,
];
