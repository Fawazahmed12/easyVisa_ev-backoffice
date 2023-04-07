import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { QuestionnaireRequestService } from '../request.service';

export const sectionWarningGetRequestHandler = createRequestHandler('GetSectionWarningRequest');

export function sectionWarningGetRequestReducer(state, action) {
  return sectionWarningGetRequestHandler.reducer(state, action);
}

@Injectable()
export class SectionWarningGetRequestEffects {

  @Effect()
  sectionWarningGetData$: Observable<Action> = sectionWarningGetRequestHandler.effect(
    this.actions$,
    this.questionnaireRequestService.questionnaireCompletionWarningRequest.bind(this.questionnaireRequestService)
  );

  constructor(
    private actions$: Actions,
    private questionnaireRequestService: QuestionnaireRequestService
  ) {
  }
}
