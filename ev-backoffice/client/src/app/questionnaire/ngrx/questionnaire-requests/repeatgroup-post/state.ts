import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { QuestionnaireRequestService } from '../request.service';

export const repeatGroupPostRequestHandler = createRequestHandler('RepeatGroup Post');

export function repeatGroupPostRequestReducer(state, action) {
  return repeatGroupPostRequestHandler.reducer(state, action);
}

@Injectable()
export class RepeatGroupPostRequestEffects {

  @Effect()
  repeatGroupPostData$: Observable<Action> = repeatGroupPostRequestHandler.effect(
    this.actions$,
    this.questionnaireRequestService.createRepeatingGroupInstance.bind(this.questionnaireRequestService)
  );

  constructor(
    private actions$: Actions,
    private questionnaireRequestService: QuestionnaireRequestService,
  ) {
  }
}
