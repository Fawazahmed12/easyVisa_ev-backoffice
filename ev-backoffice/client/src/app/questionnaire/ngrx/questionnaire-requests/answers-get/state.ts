import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { QuestionnaireRequestService } from '../request.service';

export const answersGetRequestHandler = createRequestHandler('Answers Get');

export function answersGetRequestReducer(state, action) {
  return answersGetRequestHandler.reducer(state, action);
}

@Injectable()
export class AnswersGetRequestEffects {

  @Effect()
  answersGetData$: Observable<Action> = answersGetRequestHandler.effect(
    this.actions$,
    this.questionnaireRequestService.answersGetRequest.bind(this.questionnaireRequestService)
  );

  constructor(
    private actions$: Actions,
    private questionnaireRequestService: QuestionnaireRequestService
  ) {
  }
}
