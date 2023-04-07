import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { EmailsRequestService } from '../request.service';

export const emailPostRequestHandler = createRequestHandler('PostEmailRequest');

export function emailPostRequestReducer(state, action) {
  return emailPostRequestHandler.reducer(state, action);
}

@Injectable()
export class EmailPostRequestEffects {

  @Effect()
  emailData$: Observable<Action> = emailPostRequestHandler.effect(
    this.actions$,
    this.emailsRequestService.emailPostRequest.bind(this.emailsRequestService)
  );

  constructor(
    private actions$: Actions,
    private emailsRequestService: EmailsRequestService,
  ) {
  }
}
