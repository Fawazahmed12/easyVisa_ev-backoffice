import { createFeatureSelector, createSelector } from '@ngrx/store';

import { RequestState } from '../utils';

import { Attorney } from '../../models/attorney.model';
import { Package } from '../../models/package/package.model';
import { RecipientModel } from '../../models/recipient.model';

import { packagesTransferPostRequestHandler } from './packages-transfer-post/state';
import { packagesTransferByApplicantPostRequestHandler } from './packages-transfer-by-applicant-post/state';

export const PACKAGES_REQUEST = 'PackagesRequest';

export interface PackagesRequestState {
  activePackageGet?: RequestState<Attorney[]>;
  packagesGet?: RequestState<Package[]>;
  packagesTransferPost?: RequestState<RecipientModel>;
  packagesTransferByApplicantPost?: RequestState<RecipientModel>;
}

export const selectPackagesRequestState = createFeatureSelector<PackagesRequestState>(PACKAGES_REQUEST);

export const selectActivePackageGetRequestState = createSelector(
  selectPackagesRequestState,
  (state: PackagesRequestState) => state.activePackageGet
);

export const selectPackagesGetRequestState = createSelector(
  selectPackagesRequestState,
  (state: PackagesRequestState) => state.packagesGet
);

export const selectTransferPostRequestState = createSelector(
  selectPackagesRequestState,
  (state: PackagesRequestState) => state.packagesTransferPost
);

export const selectTransferByApplicantPostRequestState = createSelector(
  selectPackagesRequestState,
  (state: PackagesRequestState) => state.packagesTransferByApplicantPost
);

export { activePackageGetRequestHandler } from './active-package-get/state';
export { packagesGetRequestHandler } from './packages-get/state';
export { packagesTransferPostRequestHandler } from './packages-transfer-post/state';
export { packagesTransferByApplicantPostRequestHandler } from './packages-transfer-by-applicant-post/state';

