import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { QuestionnaireRequestService } from '../request.service';

export const questionnaireAccessGetRequestHandler = createRequestHandler('GetQuestionnaireAccessRequest');

export function questionnaireAccessGetRequestReducer(state, action) {
  return questionnaireAccessGetRequestHandler.reducer(state, action);
}

@Injectable()
export class QuestionnaireAccessGetRequestEffects {

  @Effect()
  questionnaireAccessGetData$: Observable<Action> = questionnaireAccessGetRequestHandler.effect(
    this.actions$,
    this.questionnaireRequestService.questionnaireAccessGetRequest.bind(this.questionnaireRequestService)
  );

  constructor(
    private actions$: Actions,
    private questionnaireRequestService: QuestionnaireRequestService
  ) {
  }
}
