import { PackagesRequestState } from './state';
import { activePackageGetRequestReducer } from './active-package-get/state';
import { packagesGetRequestReducer } from './packages-get/state';
import { packagesTransferPostRequestReducer } from './packages-transfer-post/state';
import { packagesTransferByApplicantPostRequestReducer } from './packages-transfer-by-applicant-post/state';


export function reducer(state: PackagesRequestState = {}, action): PackagesRequestState {
  return {
    activePackageGet: activePackageGetRequestReducer(state.activePackageGet, action),
    packagesGet: packagesGetRequestReducer(state.packagesGet, action),
    packagesTransferPost: packagesTransferPostRequestReducer(state.packagesTransferPost, action),
    packagesTransferByApplicantPost: packagesTransferByApplicantPostRequestReducer(state.packagesTransferByApplicantPost, action),
  };
}
