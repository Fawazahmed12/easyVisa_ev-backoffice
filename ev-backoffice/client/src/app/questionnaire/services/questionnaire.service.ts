import { Injectable } from '@angular/core';

import { Action, select, Store } from '@ngrx/store';
import { FormlyFieldConfig } from '@ngx-formly/core';

import { BehaviorSubject, Observable } from 'rxjs';
import { filter, map, share } from 'rxjs/operators';

import { find } from 'lodash-es';

import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';


import {
  AnswerSaveRequest,
  AnswerValidationRequest,
  DeleteRepeatGroup,
  GetAnswers,
  GetAnswerValidation,
  GetQuestionnaireAccessState,
  GetQuestions,
  GetSections,
  GetSectionWarning,
  PostAnswer,
  PostRepeatGroup,
  RepeatGroupAddRequest,
  RepeatGroupRemoveRequest, ResetAnswerValidation
} from '../ngrx/questionnaire/questionnaire.actions';
import {
  getAddRepeatGroupRequestItem,
  getAnswerSaveRequestItem,
  getAnswersRequestState,
  getAnswerValidationData,
  getAnswerValidationRequestItem,
  getFormlyAnswer,
  getFormlyQuestionnaire,
  getPendingAnswerRequestState,
  getQuestionnaireAccessRequestState,
  getQuestionnaireAccessState,
  getQuestionnaireItems,
  getQuestionnaireSections,
  getQuestionsRequestState,
  getRemoveRepeatGroupRequestItem,
  getSectionEntities,
  getSectionsRequestState,
  getSectionWarningRequestState
} from '../ngrx/state';

import {
  Answer,
  AnswerModel,
  AnswerSaveRequestItem,
  AnswerValidationModel,
  QuestionnaireAccessState,
  QuestionnaireModel,
  QuestionnaireStateModel,
  RepeatGroupAddRequestItem,
  RepeatGroupModel,
  RepeatGroupRemoveRequestItem,
  Section,
  SectionWarningModel
} from '../models/questionnaire.model';
import { QuestionnaireEffects } from '../ngrx/questionnaire/questionnaire.effects';


@Injectable()
export class QuestionnaireService {
  questionnaireAccessGetRequest$: Observable<RequestState<QuestionnaireAccessState>>;
  sectionsGetRequest$: Observable<RequestState<any>>;
  questionsGetRequest$: Observable<RequestState<FormlyFieldConfig[]>>;
  answersGetRequest$: Observable<RequestState<Answer>>;
  answerSaveRequestItem$: Observable<AnswerSaveRequestItem>;
  addRepeatGroupRequestItem$: Observable<RepeatGroupAddRequestItem>;
  removeRepeatGroupRequestItem$: Observable<RepeatGroupRemoveRequestItem>;
  questionnaireItems$: Observable<QuestionnaireStateModel[]>;
  questionnaireSections$: Observable<QuestionnaireModel[]>;
  formlyQuestionnaire$: Observable<FormlyFieldConfig[]>;
  formlyAnswerSubject: BehaviorSubject<Answer> = new BehaviorSubject(null);
  hasPendingAnswerSaveRequest$: Observable<boolean>;

  sectionWarningGetRequest$: Observable<RequestState<SectionWarningModel>>;
  answerValidationRequestItem$: Observable<AnswerSaveRequestItem>;
  answerValidationData$: Observable<AnswerValidationModel>;
  postAnswerSuccessAction$: Observable<Action>;
  postRepeatGroupSuccessAction$: Observable<Action>;
  deleteRepeatGroupSuccessAction$: Observable<Action>;
  postRepeatGroupFailAction$: Observable<Action>;
  deleteRepeatGroupFailAction$: Observable<Action>;
  postAnswerFailAction$: Observable<Action>;
  questionnaireAccessData$: Observable<QuestionnaireAccessState>;
  private sectionEntities$: Observable<{ [ id: string ]: Section }>;
  private previousNavigationUrlState: any = { currentState: '', nextState: '' };
  private navigationData: any = { applicantId: 0, sectionId: '' };

  constructor(
    private store: Store<State>,
    private questionnaireEffects: QuestionnaireEffects,
  ) {
    this.questionnaireItems$ = this.store.pipe(select(getQuestionnaireItems));
    this.sectionEntities$ = this.store.pipe(select(getSectionEntities));
    this.answerSaveRequestItem$ = this.store.pipe(select(getAnswerSaveRequestItem));
    this.questionnaireAccessGetRequest$ = this.store.pipe(select(getQuestionnaireAccessRequestState));
    this.sectionsGetRequest$ = this.store.pipe(select(getSectionsRequestState));
    this.questionsGetRequest$ = this.store.pipe(select(getQuestionsRequestState));
    this.answersGetRequest$ = this.store.pipe(select(getAnswersRequestState));
    this.addRepeatGroupRequestItem$ = this.store.pipe(select(getAddRepeatGroupRequestItem));
    this.removeRepeatGroupRequestItem$ = this.store.pipe(select(getRemoveRepeatGroupRequestItem));
    this.questionnaireSections$ = this.store.pipe(select(getQuestionnaireSections));
    this.formlyQuestionnaire$ = this.store.pipe(select(getFormlyQuestionnaire));
    this.store.pipe(select(getFormlyAnswer)).subscribe((data: Answer) => {
      this.formlyAnswerSubject.next(data);
    });
    this.sectionWarningGetRequest$ = this.store.pipe(select(getSectionWarningRequestState));
    this.answerValidationRequestItem$ = this.store.pipe(select(getAnswerValidationRequestItem));
    this.answerValidationData$ = this.store.pipe(select(getAnswerValidationData));
    this.questionnaireAccessData$ = this.store.pipe(select(getQuestionnaireAccessState));
    this.postAnswerSuccessAction$ = this.questionnaireEffects.postAnswerSuccess$;
    this.postRepeatGroupSuccessAction$ = this.questionnaireEffects.postRepeatGroupSuccess$;
    this.deleteRepeatGroupSuccessAction$ = this.questionnaireEffects.deleteRepeatGroupSuccess$;
    this.postRepeatGroupFailAction$ = this.questionnaireEffects.postRepeatGroupFail$;
    this.deleteRepeatGroupFailAction$ = this.questionnaireEffects.deleteRepeatGroupFail$;
    this.postAnswerFailAction$ = this.questionnaireEffects.postAnswerFail$;
    this.hasPendingAnswerSaveRequest$ = this.store.pipe(select(getPendingAnswerRequestState));
  }

  getSection(id: string) {
    return this.sectionEntities$.pipe(
      map((entities) =>
        entities[ id ]
      )
    );
  }

  getQuestionnaireItem(applicantId: number): Observable<QuestionnaireStateModel> {
    return this.questionnaireItems$.pipe(
      map((items) => find(items, ['applicantId', applicantId]))
    );
  }

  getQuestionnaireAccessRequest(activePackageId) {
    this.store.dispatch(new GetQuestionnaireAccessState(activePackageId));
    return this.questionnaireAccessGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  getSections(activePackageId) {
    this.store.dispatch(new GetSections(activePackageId));
    return this.sectionsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  answerSaveRequest(answerSaveRequestState: AnswerSaveRequestItem) {
    this.store.dispatch(new AnswerSaveRequest(answerSaveRequestState));
  }

  answerValidationRequest(answerSaveRequestState: AnswerSaveRequestItem) {
    this.store.dispatch(new AnswerValidationRequest(answerSaveRequestState));
  }

  resetAnswerValidation() {
    this.store.dispatch(new ResetAnswerValidation());
  }

  saveAnswer(answerModel: AnswerModel) {
    this.setRecentNavigationState({});
    this.store.dispatch(new PostAnswer(answerModel));
  }

  repeatGroupAddRequest(addRepeatGroupRequestItem: RepeatGroupAddRequestItem) {
    this.store.dispatch(new RepeatGroupAddRequest(addRepeatGroupRequestItem));
  }


  repeatGroupRemoveRequest(removeRepeatGroupRequestItem: RepeatGroupRemoveRequestItem) {
    this.store.dispatch(new RepeatGroupRemoveRequest(removeRepeatGroupRequestItem));
  }

  addRepeatGroup(repeatGroupModel: RepeatGroupModel) {
    this.setRecentNavigationState({});
    this.store.dispatch(new PostRepeatGroup(repeatGroupModel));
  }

  removeRepeatGroup(repeatGroupModel: RepeatGroupModel) {
    this.setRecentNavigationState({});
    this.store.dispatch(new DeleteRepeatGroup(repeatGroupModel));
  }

  fetchQuestions(packageId, applicantId, sectionId) {
    this.store.dispatch(new GetQuestions({ packageId, applicantId, sectionId }));
  }

  fetchAnswers(packageId, applicantId, sectionId) {
    this.store.dispatch(new GetAnswers({ packageId, applicantId, sectionId }));
  }


  fetchSections(activePackageId) {
     this.store.dispatch(new GetSections(activePackageId));
  }

  questionsGetRequest(): Observable<FormlyFieldConfig[]> {
    return this.questionsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  answersGetRequest(): Observable<Answer> {
    return this.answersGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  sectionWarningGetRequest(packageId, applicantId, sectionId): Observable<SectionWarningModel> {
    this.store.dispatch(new GetSectionWarning({ packageId, applicantId, sectionId }));
    return this.sectionWarningGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  validateAnswer(answerModel: AnswerModel) {
    this.store.dispatch(new GetAnswerValidation(answerModel));
  }

  setRecentNavigationState(data: any) {
    this.previousNavigationUrlState = data;
  }

  getRecentNavigationState(): any {
    return this.previousNavigationUrlState;
  }

  setNavigationData(data: any) {
    this.navigationData = data;
  }

  getNavigationData(): any {
    return this.navigationData;
  }

  // This method gets invoked from both base-fieldtype.component.ts and field-request-wrapper.ts
  // If the question has any answerValidation, then this method gets invoked after getting the response
  resetAnswerValue(answerValidationData: AnswerValidationModel, field: FormlyFieldConfig) {
    const templateOptions = field.templateOptions;
    const attributes = templateOptions.attributes;
    attributes['hasQuestionAnswered'] = `${answerValidationData.hasAnswerCompleted}`;

    const fieldKey = field.key as string;
    const resetValue = answerValidationData.resetValue || '';
    field.model[fieldKey] = resetValue;
    field.formControl.patchValue(resetValue);

    this.resetAnswerValidation();
  }
}
