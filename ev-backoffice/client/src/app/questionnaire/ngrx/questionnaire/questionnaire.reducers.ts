import { adapter, QuestionnaireState } from './questionnaire.state';
import { QuestionnaireActionsUnion, QuestionnaireActionTypes } from './questionnaire.actions';
import { QuestionnaireModel, QuestionnaireStateModel, Section } from '../../models/questionnaire.model';

import { flatten, uniq } from 'lodash-es';
import { PackagesActionTypes } from '../../../core/ngrx/packages/packages.actions';

export const initialState: QuestionnaireState = adapter.getInitialState({
  questionnaireAccessState: null,
  questionnaireList: null,
  answerSaveRequestItem: null,
  answerValidationRequestItem: null,
  addRepeatGroupRequestItem: null,
  removeRepeatGroupRequestItem: null,
  questionnaireSections: null,
  formlyQuestionnaire: null,
  formlyAnswer: null,
  answerValidationData: null,
  answerRequestCorrelationCount:0,
  hasPendingAnswerSaveRequest: false
});


export function reducer(state = initialState, action: QuestionnaireActionsUnion) {
  switch (action.type) {
    case QuestionnaireActionTypes.GetQuestionnaireAccessStateSuccess: {
      const questionnaireAccessState = action.payload;
      return {
        ...state,
        questionnaireAccessState: {...questionnaireAccessState}
      };
    }
    case QuestionnaireActionTypes.GetSectionsSuccess: {
      const packageSectionsData = buildPackageSectionsData(action.payload);
      return {
        ...adapter.setAll(packageSectionsData.sections, state),
        questionnaireList: packageSectionsData.questionnaireList,
        questionnaireSections: packageSectionsData.questionnaireSections
      };
    }
    case QuestionnaireActionTypes.GetQuestions: {
      return {
        ...state,
        formlyQuestionnaire: []
      };
    }
    case QuestionnaireActionTypes.GetQuestionsSuccess: {
      const question = action.payload;
      return {
        ...state,
        formlyQuestionnaire: question
      };
    }
    case QuestionnaireActionTypes.GetAnswersSuccess: {
      const answer = action.payload;
      return {
        ...state,
        formlyAnswer: answer
      };
    }
    case QuestionnaireActionTypes.AnswerSaveRequest: {
      return {
        ...state,
        answerSaveRequestItem: { ...action.payload },
        answerValidationRequestItem: null,
      };
    }
    case QuestionnaireActionTypes.AnswerValidationRequest: {
      return {
        ...state,
        answerValidationRequestItem: { ...action.payload },
        answerSaveRequestItem: null,
      };
    }
    case QuestionnaireActionTypes.PostAnswer: {
      const newState = {
        ...state,
        hasPendingAnswerSaveRequest: true,
        answerRequestCorrelationCount:state.answerRequestCorrelationCount+1
      };
      return newState;
    }
    case QuestionnaireActionTypes.PostAnswerFailure: {
      return getNewStateAfterAnswerResponse(state);
    }
    case QuestionnaireActionTypes.PostAnswerSuccess: {
      const newState = getNewStateAfterAnswerResponse(state);
      return (newState.answerRequestCorrelationCount==0) ? buildSectionQuestionAnswerState(newState, action) : newState;
    }
    case QuestionnaireActionTypes.RepeatGroupAddRequest: {
      return {
        ...state,
        addRepeatGroupRequestItem: { ...action.payload }
      };
    }
    case QuestionnaireActionTypes.RepeatGroupRemoveRequest: {
      return {
        ...state,
        removeRepeatGroupRequestItem: { ...action.payload }
      };
    }
    case QuestionnaireActionTypes.PostRepeatGroupSuccess: {
      return buildSectionQuestionAnswerState(state, action);
    }
    case QuestionnaireActionTypes.DeleteRepeatGroupSuccess: {
      return buildSectionQuestionAnswerState(state, action);
    }
    case QuestionnaireActionTypes.GetAnswerValidation: {
      return {
        ...state,
        answerValidationData: null
      };
    }
    case QuestionnaireActionTypes.ResetAnswerValidation: {
      return {
        ...state,
        answerValidationData: null
      };
    }
    case QuestionnaireActionTypes.GetAnswerValidationSuccess: {
      return {
        ...state,
        answerValidationRequestItem: null,
        answerValidationData: { ...action.payload }
      };
    }
    case PackagesActionTypes.ClearActivePackage: {
      return{
        ...state,
        questionnaireAccessState: null,
        questionnaireList: null,
        answerSaveRequestItem: null,
        answerValidationRequestItem: null,
        addRepeatGroupRequestItem: null,
        removeRepeatGroupRequestItem: null,
        questionnaireSections: null,
        formlyQuestionnaire: null,
        formlyAnswer: null,
        answerValidationData: null
      };
    }
    default: {
      return state;
    }
  }
}

function buildSectionQuestionAnswerState(state, action) {
  const payloadData = action.payload;
  const previousSectionId = state.formlyQuestionnaire && state.formlyQuestionnaire.length ? state.formlyQuestionnaire[0].fieldId : null;
  const newSectionId = payloadData.sectionQuestions && payloadData.sectionQuestions.length ? payloadData.sectionQuestions[0].fieldId : null;
  if(previousSectionId && previousSectionId!=newSectionId) {
    return {
      ...state,
      answerSaveRequestItem: null,
      addRepeatGroupRequestItem: null,
      removeRepeatGroupRequestItem: null
    };
  }

  const packageSectionsData = buildPackageSectionsData(payloadData.packageSections);
  const sectionQuestionAnswerState = {
    ...adapter.setAll(packageSectionsData.sections, state),
    questionnaireList: packageSectionsData.questionnaireList,
    questionnaireSections: packageSectionsData.questionnaireSections,
    formlyQuestionnaire: payloadData.sectionQuestions,
    formlyAnswer: payloadData.sectionAnswer,
    answerSaveRequestItem: null,
    addRepeatGroupRequestItem: null,
    removeRepeatGroupRequestItem: null,
  };
  return sectionQuestionAnswerState;
}

function buildPackageSectionsData(packageSections) {
  const questionnaireSections: QuestionnaireModel[] = packageSections;
  const sections: Section[] = uniq(flatten(questionnaireSections.map((item) => item.sections)));
  const questionnaireList: QuestionnaireStateModel[] =
    questionnaireSections.map((item) => ({
        ...item,
        sections: item.sections.map((section) => section.id)
      })
    );
  return {
    sections,
    questionnaireList,
    questionnaireSections,
  };
}

function getNewStateAfterAnswerResponse(state) {
  const newState = {
    ...state,
    answerRequestCorrelationCount:state.answerRequestCorrelationCount-1
  };
  newState['hasPendingAnswerSaveRequest'] = newState.answerRequestCorrelationCount!=0;
  return newState;
}
