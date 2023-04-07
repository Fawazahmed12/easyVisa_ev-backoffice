import { ProgressStatusesModuleRequestState } from './state';
import { questionnaireProgressGetRequestReducer } from './questionnaire-progress-get/state';
import { documentProgressGetRequestReducer } from './document-progress-get/state';


export function reducer(state: ProgressStatusesModuleRequestState = {}, action): ProgressStatusesModuleRequestState {
  return {
    questionnaireProgressGet: questionnaireProgressGetRequestReducer(state.questionnaireProgressGet, action),
    documentProgressGet: documentProgressGetRequestReducer(state.documentProgressGet, action),
  };
}
