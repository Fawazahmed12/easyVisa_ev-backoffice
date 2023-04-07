import { RequestState } from '../../../core/ngrx/utils';

import { Package } from '../../../core/models/package/package.model';
import { PackageApplicant } from '../../../core/models/package/package-applicant.model';
import { Alert } from '../../models/alert.model';
import { Warning } from '../../models/warning.model';
import { Disposition } from '../../models/dispositions.model';

export const TASK_QUEUE_MODULE_REQUESTS = 'TaskQueueModuleRequests';

export interface TaskQueueModuleRequestState {
  applicantGet?: RequestState<PackageApplicant>;
  applicantInvitePost?: RequestState<{message: string}>;
  alertsDelete?: RequestState<number[]>;
  alertsGet?: RequestState<Alert[]>;
  alertPut?: RequestState<Alert>;
  changePackageStatusPost?: RequestState<Package>;
  packageGet?: RequestState<Package>;
  packagePatch?: RequestState<Package>;
  packagePost?: RequestState<Package>;
  packageWelcomeEmailPost?: RequestState<{message: string}>;
  retainerAgreementPost?: RequestState<{message: string}>;
  retainerAgreementDelete?: RequestState<{message: string}>;
  leadPackagesDelete?: RequestState<Package[]>;
  warningsDelete?: RequestState<number[]>;
  warningsGet?: RequestState<Warning[]>;
  warningPut?: RequestState<Warning>;
  feesBillPost?: RequestState<any>;
  dispositionsGet?: RequestState<Disposition[]>;
  dispositionDataGet?: RequestState<any>;
  dispositionPut?: RequestState<Disposition>;
  selectedLeadPackagesDelete?: RequestState<Package[]>;
  selectedTransferredPackagesDelete?: RequestState<Package[]>;
  changePackageOwedPatch?: RequestState<Package>;
}

export const selectTaskQueueModuleRequestsState = (state) => state[TASK_QUEUE_MODULE_REQUESTS];

export const selectApplicantGetState = (state: TaskQueueModuleRequestState) => state.applicantGet;
export const selectApplicantInvitePostState = (state: TaskQueueModuleRequestState) => state.applicantInvitePost;
export const selectChangePackageStatusPostState = (state: TaskQueueModuleRequestState) => state.changePackageStatusPost;
export const selectPackageGetState = (state: TaskQueueModuleRequestState) => state.packageGet;
export const selectPackagePostState = (state: TaskQueueModuleRequestState) => state.packagePost;
export const selectPackagePatchState = (state: TaskQueueModuleRequestState) => state.packagePatch;
export const selectPackageWelcomeEmailPostState = (state: TaskQueueModuleRequestState) => state.packageWelcomeEmailPost;
export const selectRetainerAgreementPostState = (state: TaskQueueModuleRequestState) => state.retainerAgreementPost;
export const selectRetainerAgreementDeleteState = (state: TaskQueueModuleRequestState) => state.retainerAgreementDelete;
export const selectLeadPackagesDeleteState = (state: TaskQueueModuleRequestState) => state.leadPackagesDelete;
export const selectWarningsDeleteState = (state: TaskQueueModuleRequestState) => state.warningsDelete;
export const selectWarningsGetState = (state: TaskQueueModuleRequestState) => state.warningsGet;
export const selectWarningPutState = (state: TaskQueueModuleRequestState) => state.warningPut;
export const selectFeesBillPostState = (state: TaskQueueModuleRequestState) => state.feesBillPost;
export const selectDispositionsGetState = (state: TaskQueueModuleRequestState) => state.dispositionsGet;
export const selectDispositionDataGetState = (state: TaskQueueModuleRequestState) => state.dispositionDataGet;
export const selectDispositionPutState = (state: TaskQueueModuleRequestState) => state.dispositionPut;
export const selectSelectedLeadPackagesDeleteState = (state: TaskQueueModuleRequestState) => state.selectedLeadPackagesDelete;
export const selectSelectedTransferredPackagesDeleteState = (state: TaskQueueModuleRequestState) => state.selectedTransferredPackagesDelete;
export const selectChangePackageOwedPatchState = (state: TaskQueueModuleRequestState) => state.changePackageOwedPatch;

export { applicantInvitePostRequestHandler } from './applicant-invite-post/state';
export { applicantGetRequestHandler } from './applicant-get/state';
export { changePackageStatusPostRequestHandler } from './change-package-status-post/state';
export { packageGetRequestHandler } from './package-get/state';
export { packagePatchRequestHandler } from './package-patch/state';
export { packagePostRequestHandler } from './package-post/state';
export { packageWelcomeEmailPostRequestHandler } from './package-welcome-email-post/state';
export { retainerAgreementPostRequestHandler } from './retainer-agreement-post/state';
export { retainerAgreementDeleteRequestHandler } from './retainer-agreement-delete/state';
export { deleteLeadPackagesRequestHandler } from './lead-packages-delete/state';
export { warningsDeleteRequestHandler } from './warnings-delete/state';
export { warningsGetRequestHandler } from './warnings-get/state';
export { warningPutRequestHandler } from './warning-put/state';
export { feesBillPostRequestHandler } from './fees-bill-post/state';
export { dispositionsGetRequestHandler } from './dispositions-get/state';
export { dispositionDataGetRequestHandler } from './disposition-data-get/state';
export { dispositionPutRequestHandler } from './disposition-put/state';
export { deleteSelectedLeadPackagesRequestHandler } from './selected-lead-packages-delete/state';
export { deleteSelectedTransferredPackagesRequestHandler } from './selected-transferred-packages-delete/state';
export { changePackageOwedPatchRequestHandler } from './change-package-owed-patch/state';
