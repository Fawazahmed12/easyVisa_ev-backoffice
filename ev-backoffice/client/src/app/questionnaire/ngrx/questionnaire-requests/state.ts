import { FormlyFieldConfig } from '@ngx-formly/core';

import {
  Answer,
  AnswerValidationModel, QuestionnaireAccessState,
  QuestionnaireModel,
  SectionWarningModel
} from '../../models/questionnaire.model';
import { RequestState } from '../../../core/ngrx/utils';


export const QUESTIONNAIRE_REQUEST = 'QuestionnaireRequests';

export interface QuestionnaireRequestState {
  questionnaireAccessGet?: RequestState<QuestionnaireAccessState>;
  sectionsGet?: RequestState<QuestionnaireModel[]>;
  questionsGet?: RequestState<FormlyFieldConfig[]>;
  answersGet?: RequestState<Answer>;
  answerPost?: RequestState<any>;
  repeatGroupPost?: RequestState<any>;
  repeatGroupDelete?: RequestState<any>;
  sectionWarningGet?: RequestState<SectionWarningModel>;
  answerValidationGet?: RequestState<AnswerValidationModel>;
}

export const selectQuestionnaireRequestState = (state) => state[ QUESTIONNAIRE_REQUEST ];

export const selectQuestionnaireAccessGetState = (state: QuestionnaireRequestState) => state.questionnaireAccessGet;
export const selectSectionsGetState = (state: QuestionnaireRequestState) => state.sectionsGet;
export const selectQuestionsGetState = (state: QuestionnaireRequestState) => state.questionsGet;
export const selectAnswersGetState = (state: QuestionnaireRequestState) => state.answersGet;
export const selectAnswerPostState = (state: QuestionnaireRequestState) => state.answerPost;
export const selectRepeatGroupPostState = (state: QuestionnaireRequestState) => state.repeatGroupPost;
export const selectRepeatGroupDeleteState = (state: QuestionnaireRequestState) => state.repeatGroupDelete;
export const selectSectionWarningGetState = (state: QuestionnaireRequestState) => state.sectionWarningGet;
export const selectAnswerValidationGetState = (state: QuestionnaireRequestState) => state.answerValidationGet;

export { questionnaireAccessGetRequestHandler } from './questionnaireaccess-get/state';
export { sectionsGetRequestHandler } from './sections-get/state';
export { questionsGetRequestHandler } from './questions-get/state';
export { answersGetRequestHandler } from './answers-get/state';
export { answerPostRequestHandler } from './answer-post/state';
export { repeatGroupPostRequestHandler } from './repeatgroup-post/state';
export { repeatGroupDeleteRequestHandler } from './repeatgroup-delete/state';
export { sectionWarningGetRequestHandler } from './sectionwarning-get/state';
export { answerValidationGetRequestHandler } from './answervalidation-get/state';
