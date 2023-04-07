import { FeeDetailsGetRequestEffects } from './fee-details-get/state';
import { FeeDetailsPostRequestEffects } from './fee-details-post/state';
import { GovernmentFeeGetRequestEffects } from './government-fee-get/state';
import { BenefitsGetRequestEffects } from './benefits-get/state';

export const ConfigDataRequestEffects = [
  FeeDetailsGetRequestEffects,
  FeeDetailsPostRequestEffects,
  GovernmentFeeGetRequestEffects,
  BenefitsGetRequestEffects
];
