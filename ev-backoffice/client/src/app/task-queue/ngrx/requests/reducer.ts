import { TaskQueueModuleRequestState } from './state';
import { applicantGetRequestReducer } from './applicant-get/state';
import { applicantInvitePostRequestReducer } from './applicant-invite-post/state';
import { alertsDeleteRequestReducer } from '../../../core/ngrx/alerts-requests/alerts-delete/state';
import { alertsGetRequestReducer } from '../../../core/ngrx/alerts-requests/alerts-get/state';
import { alertPutRequestReducer } from '../../../core/ngrx/alerts-requests/alert-put/state';
import { changePackageStatusPostRequestReducer } from './change-package-status-post/state';
import { changePackageOwedPatchRequestReducer } from './change-package-owed-patch/state';
import { packageRequestReducer } from './package-get/state';
import { packagePostRequestReducer } from './package-post/state';
import { packagePatchRequestReducer } from './package-patch/state';
import { packageWelcomeEmailPostRequestReducer } from './package-welcome-email-post/state';
import { retainerAgreementPostRequestReducer } from './retainer-agreement-post/state';
import { retainerAgreementDeleteRequestReducer } from './retainer-agreement-delete/state';
import { leadPackagesDeleteRequestReducer } from './lead-packages-delete/state';
import { warningsDeleteRequestReducer } from './warnings-delete/state';
import { warningsGetRequestReducer } from './warnings-get/state';
import { warningPutRequestReducer } from './warning-put/state';
import { feesBillPostRequestReducer } from './fees-bill-post/state';
import { dispositionsGetRequestReducer } from './dispositions-get/state';
import { dispositionDataGetRequestReducer } from './disposition-data-get/state';
import { dispositionPutRequestReducer } from './disposition-put/state';
import { selectedLeadPackagesDeleteRequestReducer } from './selected-lead-packages-delete/state';
import { selectedTransferredPackagesDeleteRequestReducer } from './selected-transferred-packages-delete/state';

export function reducer(state: TaskQueueModuleRequestState = {}, action): TaskQueueModuleRequestState {
  return {
    applicantGet: applicantGetRequestReducer(state.applicantGet, action),
    applicantInvitePost: applicantInvitePostRequestReducer(state.applicantInvitePost, action),
    alertsDelete: alertsDeleteRequestReducer(state.alertsDelete, action),
    alertsGet: alertsGetRequestReducer(state.alertsGet, action),
    alertPut: alertPutRequestReducer(state.alertPut, action),
    changePackageStatusPost: changePackageStatusPostRequestReducer(state.changePackageStatusPost, action),
    packageGet: packageRequestReducer(state.packageGet, action),
    packagePatch: packagePatchRequestReducer(state.packagePatch, action),
    packagePost: packagePostRequestReducer(state.packagePost, action),
    packageWelcomeEmailPost: packageWelcomeEmailPostRequestReducer(state.packageWelcomeEmailPost, action),
    retainerAgreementPost: retainerAgreementPostRequestReducer(state.retainerAgreementPost, action),
    retainerAgreementDelete: retainerAgreementDeleteRequestReducer(state.retainerAgreementDelete, action),
    leadPackagesDelete: leadPackagesDeleteRequestReducer(state.leadPackagesDelete, action),
    warningsDelete: warningsDeleteRequestReducer(state.warningsDelete, action),
    warningsGet: warningsGetRequestReducer(state.warningsGet, action),
    warningPut: warningPutRequestReducer(state.warningPut, action),
    feesBillPost: feesBillPostRequestReducer(state.feesBillPost, action),
    dispositionsGet: dispositionsGetRequestReducer(state.dispositionsGet, action),
    dispositionDataGet: dispositionDataGetRequestReducer(state.dispositionDataGet, action),
    dispositionPut: dispositionPutRequestReducer(state.dispositionPut, action),
    selectedLeadPackagesDelete: selectedLeadPackagesDeleteRequestReducer(state.selectedLeadPackagesDelete, action),
    selectedTransferredPackagesDelete: selectedTransferredPackagesDeleteRequestReducer(state.selectedTransferredPackagesDelete, action),
    changePackageOwedPatch: changePackageOwedPatchRequestReducer(state.changePackageOwedPatch, action),
  };
}
