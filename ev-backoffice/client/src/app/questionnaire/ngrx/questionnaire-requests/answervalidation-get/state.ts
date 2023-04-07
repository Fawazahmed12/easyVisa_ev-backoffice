import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { QuestionnaireRequestService } from '../request.service';

export const answerValidationGetRequestHandler = createRequestHandler('GetAnswerValidationRequest');

export function answerValidationGetRequestReducer(state, action) {
  return answerValidationGetRequestHandler.reducer(state, action);
}

@Injectable()
export class AnswerValidationGetRequestEffects {

  @Effect()
  answerValidationGetData$: Observable<Action> = answerValidationGetRequestHandler.effect(
    this.actions$,
    this.questionnaireRequestService.answerValidationRequest.bind(this.questionnaireRequestService)
  );

  constructor(
    private actions$: Actions,
    private questionnaireRequestService: QuestionnaireRequestService
  ) {
  }
}
