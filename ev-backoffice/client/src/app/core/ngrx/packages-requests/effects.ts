import { ActivePackageGetRequestEffects } from './active-package-get/state';
import { PackagesGetRequestEffects } from './packages-get/state';
import { PackagesTransferPostRequestEffects } from './packages-transfer-post/state';
import { PackagesTransferByApplicantPostRequestEffects } from './packages-transfer-by-applicant-post/state';

export const PackagesRequestEffects = [
  ActivePackageGetRequestEffects,
  PackagesGetRequestEffects,
  PackagesTransferPostRequestEffects,
  PackagesTransferByApplicantPostRequestEffects,
];
