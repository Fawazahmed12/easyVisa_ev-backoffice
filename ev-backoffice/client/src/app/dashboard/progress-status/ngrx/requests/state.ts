import { RequestState } from '../../../../core/ngrx/utils';

import { ProgressStatus } from '../../models/progress-status.model';

export const PROGRESS_STATUSES_MODULE_REQUESTS = 'ProgressStatusesModuleRequests';

export interface ProgressStatusesModuleRequestState {
  questionnaireProgressGet?: RequestState<ProgressStatus[]>;
  documentProgressGet?: RequestState<ProgressStatus[]>;
}

export const selectProgressStatusesModuleRequestsState = (state) => state[PROGRESS_STATUSES_MODULE_REQUESTS];

export const selectQuestionnaireProgressGetState = (state: ProgressStatusesModuleRequestState) => state.questionnaireProgressGet;
export const selectDocumentProgressGetState = (state: ProgressStatusesModuleRequestState) => state.documentProgressGet;

export { questionnaireProgressGetRequestHandler } from './questionnaire-progress-get/state';
export { documentProgressGetRequestHandler } from './document-progress-get/state';

