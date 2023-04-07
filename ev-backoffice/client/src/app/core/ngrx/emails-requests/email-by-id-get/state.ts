import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { EmailsRequestService } from '../request.service';

export const emailByIdGetRequestHandler = createRequestHandler('GetEmailByIdRequest');

export function emailByIdGetRequestReducer(state, action) {
  return emailByIdGetRequestHandler.reducer(state, action);
}

@Injectable()
export class EmailByIdGetRequestEffects {

  @Effect()
  emailByIdData$: Observable<Action> = emailByIdGetRequestHandler.effect(
    this.actions$,
    this.emailsRequestService.emailByIdGetRequest.bind(this.emailsRequestService)
  );

  constructor(
    private actions$: Actions,
    private emailsRequestService: EmailsRequestService,
  ) {
  }
}
