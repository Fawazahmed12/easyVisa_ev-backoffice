import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { QuestionnaireRequestService } from '../request.service';

export const repeatGroupDeleteRequestHandler = createRequestHandler('RepeatGroup Delete');

export function repeatGroupDeleteRequestReducer(state, action) {
  return repeatGroupDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class RepeatGroupDeleteRequestEffects {

  @Effect()
  repeatGroupDeleteData$: Observable<Action> = repeatGroupDeleteRequestHandler.effect(
    this.actions$,
    this.questionnaireRequestService.removeRepeatingGroupInstance.bind(this.questionnaireRequestService)
  );

  constructor(
    private actions$: Actions,
    private questionnaireRequestService: QuestionnaireRequestService,
  ) {
  }
}
