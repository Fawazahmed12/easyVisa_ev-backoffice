import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { QuestionnaireRequestService } from '../request.service';

export const answerPostRequestHandler = createRequestHandler('Answer Post', 'concat');

export function answerPostRequestReducer(state, action) {
  return answerPostRequestHandler.reducer(state, action);
}

@Injectable()
export class AnswerPostRequestEffects {

  @Effect()
  answerPostData$: Observable<Action> = answerPostRequestHandler.effect(
    this.actions$,
    this.questionnaireRequestService.answerPostRequest.bind(this.questionnaireRequestService)
  );

  constructor(
    private actions$: Actions,
    private questionnaireRequestService: QuestionnaireRequestService,
  ) {
  }
}
