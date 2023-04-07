import { createFeatureSelector, createSelector } from '@ngrx/store';

import * as fromQuestionnaire from './questionnaire/questionnaire.state';
import {
  selectAddRepeatGroupRequestItem,
  selectAnswerSaveRequestItem,
  selectAnswerValidationData,
  selectAnswerValidationRequestItem,
  selectFormlyAnswer,
  selectFormlyQuestionnaire, selectPendingAnswerSaveRequest,
  selectQuestionnaireAccessState,
  selectQuestionnaireItems,
  selectQuestionnaireSections,
  selectQuestionnaireState,
  selectRemoveRepeatGroupRequestItem,
  selectSectionEntities
} from './questionnaire/questionnaire.state';
import * as fromQuestionnaireRequest from './questionnaire-requests/state';
import {
  selectAnswersGetState, selectQuestionnaireAccessGetState,
  selectQuestionnaireRequestState,
  selectQuestionsGetState,
  selectSectionsGetState,
  selectSectionWarningGetState
} from './questionnaire-requests/state';

export interface State {
  [ fromQuestionnaire.QUESTIONNAIRE_STATE ]: fromQuestionnaire.QuestionnaireState;
  [ fromQuestionnaireRequest.QUESTIONNAIRE_REQUEST ]: fromQuestionnaireRequest.QuestionnaireRequestState;
}

export const QUESTIONNAIRE_MODULE_STATE = 'QuestionnaireModuleState';
export const selectQuestionnaireModuleState = createFeatureSelector<State>(QUESTIONNAIRE_MODULE_STATE);

export const getQuestionnaireState = createSelector(
  selectQuestionnaireModuleState,
  selectQuestionnaireState,
);

export const getSectionEntities = createSelector(
  getQuestionnaireState,
  selectSectionEntities,
);

export const getQuestionnaireItems = createSelector(
  getQuestionnaireState,
  selectQuestionnaireItems,
);

export const getAnswerSaveRequestItem = createSelector(
  getQuestionnaireState,
  selectAnswerSaveRequestItem,
);


export const getAnswerValidationRequestItem = createSelector(
  getQuestionnaireState,
  selectAnswerValidationRequestItem,
);

export const getQuestionnaireRequestState = createSelector(
  selectQuestionnaireModuleState,
  selectQuestionnaireRequestState,
);

export const getQuestionnaireAccessRequestState = createSelector(
  getQuestionnaireRequestState,
  selectQuestionnaireAccessGetState,
);


export const getSectionsRequestState = createSelector(
  getQuestionnaireRequestState,
  selectSectionsGetState,
);


export const getQuestionsRequestState = createSelector(
  getQuestionnaireRequestState,
  selectQuestionsGetState,
);

export const getAnswersRequestState = createSelector(
  getQuestionnaireRequestState,
  selectAnswersGetState,
);


export const getSectionWarningRequestState = createSelector(
  getQuestionnaireRequestState,
  selectSectionWarningGetState,
);



export const getQuestionnaireAccessState = createSelector(
  getQuestionnaireState,
  selectQuestionnaireAccessState,
);


export const getAddRepeatGroupRequestItem = createSelector(
  getQuestionnaireState,
  selectAddRepeatGroupRequestItem,
);

export const getRemoveRepeatGroupRequestItem = createSelector(
  getQuestionnaireState,
  selectRemoveRepeatGroupRequestItem,
);

export const getQuestionnaireSections = createSelector(
  getQuestionnaireState,
  selectQuestionnaireSections,
);

export const getFormlyQuestionnaire = createSelector(
  getQuestionnaireState,
  selectFormlyQuestionnaire,
);

export const getFormlyAnswer = createSelector(
  getQuestionnaireState,
  selectFormlyAnswer,
);

export const getAnswerValidationData = createSelector(
  getQuestionnaireState,
  selectAnswerValidationData,
);

export const getPendingAnswerRequestState = createSelector(
  getQuestionnaireState,
  selectPendingAnswerSaveRequest,
);
