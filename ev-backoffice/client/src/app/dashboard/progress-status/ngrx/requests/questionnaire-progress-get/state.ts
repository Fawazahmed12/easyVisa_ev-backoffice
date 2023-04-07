import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';
import { ProgressStatusesModuleRequestService } from '../request.service';

export const questionnaireProgressGetRequestHandler = createRequestHandler('GetQuestionnaireProgress');

export function questionnaireProgressGetRequestReducer(state, action) {
  return questionnaireProgressGetRequestHandler.reducer(state, action);
}

@Injectable()
export class QuestionnaireProgressGetRequestEffects {

  @Effect()
  questionnaireProgress$: Observable<Action> = questionnaireProgressGetRequestHandler.effect(
    this.actions$,
    this.progressStatusesModuleRequestService.questionnaireProgressGetRequest.bind(this.progressStatusesModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private progressStatusesModuleRequestService: ProgressStatusesModuleRequestService,
  ) {
  }
}
