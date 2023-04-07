import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { QuestionnaireRequestService } from '../request.service';

export const sectionsGetRequestHandler = createRequestHandler('GetSectionsRequest');

export function sectionsGetRequestReducer(state, action) {
  return sectionsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class SectionsGetRequestEffects {

  @Effect()
  sectionsGetData$: Observable<Action> = sectionsGetRequestHandler.effect(
    this.actions$,
    this.questionnaireRequestService.sectionsGetRequest.bind(this.questionnaireRequestService)
  );

  constructor(
    private actions$: Actions,
    private questionnaireRequestService: QuestionnaireRequestService
  ) {
  }
}
