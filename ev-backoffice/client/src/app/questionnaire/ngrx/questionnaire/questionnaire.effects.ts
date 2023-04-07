import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';
import { FormlyFieldConfig } from '@ngx-formly/core';

import { map, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';

import {
  Answer,
  AnswerValidationModel,
  QuestionnaireModel,
  SectionWarningModel
} from '../../models/questionnaire.model';
import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';

import {
  DeleteRepeatGroup,
  DeleteRepeatGroupFailure,
  DeleteRepeatGroupSuccess,
  GetAnswers,
  GetAnswersFailure,
  GetAnswersSuccess,
  GetAnswerValidation,
  GetAnswerValidationFailure,
  GetAnswerValidationSuccess,
  GetQuestionnaireAccessStateFailure,
  GetQuestionnaireAccessStateSuccess,
  GetQuestions,
  GetQuestionsFailure,
  GetQuestionsSuccess,
  GetSections,
  GetSectionsFailure,
  GetSectionsSuccess,
  GetSectionWarning,
  GetSectionWarningFailure,
  GetSectionWarningSuccess,
  PostAnswer,
  PostAnswerFailure,
  PostAnswerSuccess,
  PostRepeatGroup,
  PostRepeatGroupFailure,
  PostRepeatGroupSuccess,
  QuestionnaireActionTypes,
} from './questionnaire.actions';

import { questionnaireAccessGetRequestHandler } from '../questionnaire-requests/questionnaireaccess-get/state';
import { sectionsGetRequestHandler } from '../questionnaire-requests/sections-get/state';
import { questionsGetRequestHandler } from '../questionnaire-requests/questions-get/state';
import { answersGetRequestHandler } from '../questionnaire-requests/answers-get/state';
import { answerPostRequestHandler } from '../questionnaire-requests/answer-post/state';
import { repeatGroupPostRequestHandler } from '../questionnaire-requests/repeatgroup-post/state';
import { repeatGroupDeleteRequestHandler } from '../questionnaire-requests/repeatgroup-delete/state';
import { sectionWarningGetRequestHandler } from '../questionnaire-requests/sectionwarning-get/state';
import { answerValidationGetRequestHandler } from '../questionnaire-requests/answervalidation-get/state';
import { I18nActionTypes, LangChange } from '../../../core/i18n/i18n.actions';
import { PackagesService } from '../../../core/services';

@Injectable()
export class QuestionnaireEffects {

  @Effect()
  getQuestionnaireAccess$: Observable<Action> = this.actions$.pipe(
    ofType(QuestionnaireActionTypes.GetQuestionnaireAccessState),
    map(({ payload }: GetSections) => questionnaireAccessGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getQuestionnaireAccess$Success$: Observable<Action> = this.actions$.pipe(
    ofType(questionnaireAccessGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<QuestionnaireModel[]>) => new GetQuestionnaireAccessStateSuccess(payload))
  );

  @Effect()
  getQuestionnaireAccess$Failure$: Observable<Action> = this.actions$.pipe(
    ofType(questionnaireAccessGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetQuestionnaireAccessStateFailure(payload))
  );

  @Effect()
  getSections$: Observable<Action> = this.actions$.pipe(
    ofType(QuestionnaireActionTypes.GetSections),
    map(({ payload }: GetSections) => sectionsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getSectionsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(sectionsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<QuestionnaireModel[]>) => new GetSectionsSuccess(payload))
  );

  @Effect()
  getSectionsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(sectionsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetSectionsFailure(payload))
  );

  @Effect()
  getQuestions$: Observable<Action> = this.actions$.pipe(
    ofType(QuestionnaireActionTypes.GetQuestions),
    map(({ payload }: GetQuestions) => questionsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getQuestionsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(questionsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<FormlyFieldConfig[]>) => new GetQuestionsSuccess(payload))
  );

  @Effect()
  getQuestionsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(questionsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetQuestionsFailure(payload))
  );

  @Effect()
  getAnswers$: Observable<Action> = this.actions$.pipe(
    ofType(QuestionnaireActionTypes.GetAnswers),
    map(({ payload }: GetAnswers) => answersGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getAnswersSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(answersGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<Answer>) => new GetAnswersSuccess(payload))
  );

  @Effect()
  getAnswersFailure$: Observable<Action> = this.actions$.pipe(
    ofType(answersGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetAnswersFailure(payload))
  );

  @Effect()
  postAnswer$: Observable<Action> = this.actions$.pipe(
    ofType(QuestionnaireActionTypes.PostAnswer),
    map(({ payload }: PostAnswer) => answerPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postAnswerSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(answerPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new PostAnswerSuccess(payload))
  );

  @Effect({ dispatch: false })
  postAnswerFail$: Observable<Action> = this.actions$.pipe(
    ofType(answerPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new PostAnswerFailure(payload))
  );

  @Effect()
  postRepeatGroup$: Observable<Action> = this.actions$.pipe(
    ofType(QuestionnaireActionTypes.PostRepeatGroup),
    map(({ payload }: PostRepeatGroup) => repeatGroupPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postRepeatGroupSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(repeatGroupPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new PostRepeatGroupSuccess(payload))
  );

  @Effect({ dispatch: false })
  postRepeatGroupFail$: Observable<Action> = this.actions$.pipe(
    ofType(repeatGroupPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new PostRepeatGroupFailure(payload))
  );

  @Effect()
  deleteRepeatGroup$: Observable<Action> = this.actions$.pipe(
    ofType(QuestionnaireActionTypes.DeleteRepeatGroup),
    map(({ payload }: DeleteRepeatGroup) => repeatGroupDeleteRequestHandler.requestAction(payload))
  );

  @Effect()
  deleteRepeatGroupSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(repeatGroupDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new DeleteRepeatGroupSuccess(payload))
  );

  @Effect({ dispatch: false })
  deleteRepeatGroupFail$: Observable<Action> = this.actions$.pipe(
    ofType(repeatGroupDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new DeleteRepeatGroupFailure(payload))
  );

  @Effect()
  getSectionWarning$: Observable<Action> = this.actions$.pipe(
    ofType(QuestionnaireActionTypes.GetSectionWarning),
    map(({ payload }: GetSectionWarning) => sectionWarningGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getSectionWarningSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(sectionWarningGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<SectionWarningModel>) => new GetSectionWarningSuccess(payload))
  );

  @Effect()
  getSectionWarningFailure$: Observable<Action> = this.actions$.pipe(
    ofType(sectionWarningGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetSectionWarningFailure(payload))
  );

  @Effect()
  getAnswerValidation$: Observable<Action> = this.actions$.pipe(
    ofType(QuestionnaireActionTypes.GetAnswerValidation),
    map(({ payload }: GetAnswerValidation) => answerValidationGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getAnswerValidationSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(answerValidationGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<AnswerValidationModel>) => new GetAnswerValidationSuccess(payload))
  );

  @Effect()
  getAnswerValidationFailure$: Observable<Action> = this.actions$.pipe(
    ofType(answerValidationGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetAnswerValidationFailure(payload))
  );

  @Effect()
  loadQuestionnaireOnLangChange$: Observable<Action> = this.actions$.pipe(
    ofType(I18nActionTypes.LangChange),
    withLatestFrom(
      this.packagesService.activePackageId$
    ),
    map(([ action, activePackageId ]: [ LangChange, number ]) => new GetSections(activePackageId.toString()))
  );


  constructor(private actions$: Actions, private packagesService: PackagesService) {
  }
}
