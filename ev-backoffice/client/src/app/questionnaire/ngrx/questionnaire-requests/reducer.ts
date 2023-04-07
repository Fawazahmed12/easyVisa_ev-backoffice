import { QuestionnaireRequestState } from './state';
import { sectionsGetRequestReducer } from './sections-get/state';
import { questionsGetRequestReducer } from './questions-get/state';
import { answersGetRequestReducer } from './answers-get/state';
import { answerPostRequestReducer } from './answer-post/state';
import { repeatGroupPostRequestReducer } from './repeatgroup-post/state';
import { repeatGroupDeleteRequestReducer } from './repeatgroup-delete/state';
import { sectionWarningGetRequestReducer } from './sectionwarning-get/state';
import { answerValidationGetRequestReducer } from './answervalidation-get/state';
import { questionnaireAccessGetRequestReducer } from './questionnaireaccess-get/state';

export function reducer(state: QuestionnaireRequestState = {}, action) {
  return {
    questionnaireAccessGet: questionnaireAccessGetRequestReducer(state.questionnaireAccessGet, action),
    sectionsGet: sectionsGetRequestReducer(state.sectionsGet, action),
    questionsGet: questionsGetRequestReducer(state.questionsGet, action),
    answersGet: answersGetRequestReducer(state.answersGet, action),
    answerPost: answerPostRequestReducer(state.answerPost, action),
    repeatGroupPost: repeatGroupPostRequestReducer(state.repeatGroupPost, action),
    repeatGroupDelete: repeatGroupDeleteRequestReducer(state.repeatGroupDelete, action),
    sectionWarningGet: sectionWarningGetRequestReducer(state.sectionWarningGet, action),
    answerValidationGet: answerValidationGetRequestReducer(state.answerValidationGet, action),
  };
}
