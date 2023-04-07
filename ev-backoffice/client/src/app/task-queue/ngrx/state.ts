import { createFeatureSelector, createSelector } from '@ngrx/store';

import {
  selectApplicantGetState,
  selectApplicantInvitePostState, selectChangePackageOwedPatchState,
  selectChangePackageStatusPostState,
  selectDispositionDataGetState,
  selectDispositionPutState,
  selectDispositionsGetState,
  selectFeesBillPostState,
  selectLeadPackagesDeleteState,
  selectPackageGetState,
  selectPackagePatchState,
  selectPackagePostState,
  selectPackageWelcomeEmailPostState,
  selectRetainerAgreementDeleteState,
  selectRetainerAgreementPostState, selectSelectedLeadPackagesDeleteState, selectSelectedTransferredPackagesDeleteState,
  selectTaskQueueModuleRequestsState,
  selectWarningPutState,
  selectWarningsDeleteState,
  selectWarningsGetState,
  TASK_QUEUE_MODULE_REQUESTS,
  TaskQueueModuleRequestState
} from './requests/state';

import {
  selectActiveWarning,
  selectActiveWarningId,
  selectTotalWarnings,
  selectWarnings,
  selectWarningsEntities,
  selectWarningsState,
  WARNINGS,
  WarningsState
} from './warnings/warnings.state';
import {
  DISPOSITIONS,
  DispositionsState, selectActiveDisposition,
  selectActiveDispositionId, selectDispositionData,
  selectDispositions,
  selectDispositionsEntities,
  selectDispositionsState, selectTotalDispositions,
} from './dispositions/dispositions.state';

export const TASK_QUEUE_MODULE_STATE = 'TaskQueueModuleState';

export interface State {
  [WARNINGS]: WarningsState;
  [DISPOSITIONS]: DispositionsState;
  [TASK_QUEUE_MODULE_REQUESTS]: TaskQueueModuleRequestState;
}

export const selectTaskQueueModuleState = createFeatureSelector<State>(TASK_QUEUE_MODULE_STATE);

export const getTaskQueueModuleRequestsState = createSelector(
  selectTaskQueueModuleState,
  selectTaskQueueModuleRequestsState,
);

export const getApplicantRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectApplicantGetState,
);

export const patchPackageRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectPackagePatchState,
);

export const postPackageRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectPackagePostState,
);

export const getPackageRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectPackageGetState,
);

export const getRetainerAgreementPostRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectRetainerAgreementPostState,
);

export const getRetainerAgreementDeleteRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectRetainerAgreementDeleteState,
);

export const postPackageWelcomeEmailRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectPackageWelcomeEmailPostState,
);

export const getApplicantInvitePostRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectApplicantInvitePostState,
);

export const getChangePackageStatusPostRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectChangePackageStatusPostState,
);
export const getChangePackageOwedPatchRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectChangePackageOwedPatchState,
);

export const deleteLeadPackagesRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectLeadPackagesDeleteState,
);

export const getWarningsDeleteRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectWarningsDeleteState,
);

export const getWarningsGetRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectWarningsGetState,
);

export const getWarningPutRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectWarningPutState,
);

export const getWarningsState = createSelector(
  selectTaskQueueModuleState,
  selectWarningsState,
);

export const getWarningsData = createSelector(
  getWarningsState,
  selectWarnings,
);

export const getWarningsEntities = createSelector(
  getWarningsState,
  selectWarningsEntities,
);

export const getActiveWarningId = createSelector(
  getWarningsState,
  selectActiveWarningId,
);

export const getTotalWarnings = createSelector(
  getWarningsState,
  selectTotalWarnings,
);

export const getActiveWarning = createSelector(
  getWarningsState,
  selectActiveWarning,
);

export const getFeesBillPostRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectFeesBillPostState,
);

export const getDispositionsState = createSelector(
  selectTaskQueueModuleState,
  selectDispositionsState
);

export const getDispositions = createSelector(
  getDispositionsState,
  selectDispositions,
);

export const getDispositionsEntities = createSelector(
  getDispositionsState,
  selectDispositionsEntities,
);

export const getActiveDispositionId = createSelector(
  getDispositionsState,
  selectActiveDispositionId,
);

export const getActiveDisposition = createSelector(
  getDispositionsState,
  selectActiveDisposition,
);

export const getActiveDispositionData = createSelector(
  getDispositionsState,
  selectDispositionData,
);

export const getTotalDispositions = createSelector(
  getDispositionsState,
  selectTotalDispositions,
);

export const getDispositionsGetRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectDispositionsGetState,
);

export const getDispositionDataGetRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectDispositionDataGetState,
);

export const getDispositionPutRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectDispositionPutState,
);


export const deleteSelectedLeadPackagesRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectSelectedLeadPackagesDeleteState,
);


export const deleteSelectedTransferredPackagesRequestState = createSelector(
  getTaskQueueModuleRequestsState,
  selectSelectedTransferredPackagesDeleteState,
);
