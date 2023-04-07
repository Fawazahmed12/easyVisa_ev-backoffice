import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { QuestionnaireRequestService } from '../request.service';

export const questionsGetRequestHandler = createRequestHandler('GetQuestionsRequest');

export function questionsGetRequestReducer(state, action) {
  return questionsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class QuestionsGetRequestEffects {

  @Effect()
  questionsGetData$: Observable<Action> = questionsGetRequestHandler.effect(
    this.actions$,
    this.questionnaireRequestService.questionsGetRequest.bind(this.questionnaireRequestService)
  );

  constructor(
    private actions$: Actions,
    private questionnaireRequestService: QuestionnaireRequestService
  ) {
  }
}
