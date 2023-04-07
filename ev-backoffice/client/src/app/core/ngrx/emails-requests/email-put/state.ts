import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { EmailsRequestService } from '../request.service';

export const emailPutRequestHandler = createRequestHandler('PutEmailRequest');

export function emailPutRequestReducer(state, action) {
  return emailPutRequestHandler.reducer(state, action);
}

@Injectable()
export class EmailPutRequestEffects {

  @Effect()
  emailData$: Observable<Action> = emailPutRequestHandler.effect(
    this.actions$,
    this.emailsRequestService.emailPutRequest.bind(this.emailsRequestService)
  );

  constructor(
    private actions$: Actions,
    private emailsRequestService: EmailsRequestService,
  ) {
  }
}
