import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { EmailTemplatesRequestService } from '../request.service';

export const emailTemplatesGetRequestHandler = createRequestHandler('Email Templates Get');

export function emailTemplatesGetRequestReducer(state, action) {
  return emailTemplatesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class EmailTemplatesGetRequestEffects {

  @Effect()
  emailTemplatesGet$: Observable<Action> = emailTemplatesGetRequestHandler.effect(
    this.actions$,
    this.emailTemplatesRequestService.emailTemplatesGetRequest.bind(this.emailTemplatesRequestService)
  );

  constructor(
    private actions$: Actions,
    private emailTemplatesRequestService: EmailTemplatesRequestService
  ) {
  }
}
