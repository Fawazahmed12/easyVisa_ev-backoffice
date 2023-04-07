import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector } from '@ngrx/store';
import { FormlyFieldConfig } from '@ngx-formly/core';


import {
  Answer,
  AnswerSaveRequestItem, AnswerValidationModel, QuestionnaireAccessState,
  QuestionnaireModel,
  QuestionnaireStateModel,
  RepeatGroupAddRequestItem,
  RepeatGroupRemoveRequestItem,
  Section,
} from '../../models/questionnaire.model';


export const QUESTIONNAIRE_STATE = 'Questionnaire';


export interface QuestionnaireState extends EntityState<Section> {
  questionnaireAccessState: QuestionnaireAccessState;
  questionnaireList: QuestionnaireStateModel[];
  answerSaveRequestItem: AnswerSaveRequestItem;
  answerValidationRequestItem: AnswerSaveRequestItem;
  addRepeatGroupRequestItem: RepeatGroupAddRequestItem;
  removeRepeatGroupRequestItem: RepeatGroupRemoveRequestItem;
  questionnaireSections: QuestionnaireModel[];
  formlyQuestionnaire: FormlyFieldConfig[];
  formlyAnswer: Answer;
  answerValidationData: AnswerValidationModel;
  answerRequestCorrelationCount: number;
  hasPendingAnswerSaveRequest: boolean;
}

export const adapter: EntityAdapter<Section> = createEntityAdapter<Section>();

const questionnaireSelectors = adapter.getSelectors();

export const selectQuestionnaireState = createFeatureSelector<QuestionnaireState>(QUESTIONNAIRE_STATE);

export const selectSectionEntities = questionnaireSelectors.selectEntities;

export const selectQuestionnaireAccessState = (state: QuestionnaireState) => state.questionnaireAccessState;

export const selectQuestionnaireItems = (state: QuestionnaireState) => state.questionnaireList;

export const selectAnswerSaveRequestItem = (state: QuestionnaireState) => state.answerSaveRequestItem;

export const selectAnswerValidationRequestItem = (state: QuestionnaireState) => state.answerValidationRequestItem;

export const selectAddRepeatGroupRequestItem = (state: QuestionnaireState) => state.addRepeatGroupRequestItem;

export const selectRemoveRepeatGroupRequestItem = (state: QuestionnaireState) => state.removeRepeatGroupRequestItem;

export const selectQuestionnaireSections = (state: QuestionnaireState) => state.questionnaireSections;

export const selectFormlyQuestionnaire = (state: QuestionnaireState) => state.formlyQuestionnaire;

export const selectFormlyAnswer = (state: QuestionnaireState) => state.formlyAnswer;

export const selectAnswerValidationData = (state: QuestionnaireState) => state.answerValidationData;

export const selectPendingAnswerSaveRequest = (state: QuestionnaireState) => state.hasPendingAnswerSaveRequest;
