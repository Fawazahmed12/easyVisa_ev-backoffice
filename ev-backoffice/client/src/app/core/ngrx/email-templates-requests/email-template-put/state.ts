import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { EmailTemplatesRequestService } from '../request.service';

export const emailTemplatePutRequestHandler = createRequestHandler('Email Template Put');

export function emailTemplatePutRequestReducer(state, action) {
  return emailTemplatePutRequestHandler.reducer(state, action);
}

@Injectable()
export class EmailTemplatePutRequestEffects {

  @Effect()
  emailTemplatePut$: Observable<Action> = emailTemplatePutRequestHandler.effect(
    this.actions$,
    this.emailTemplatesRequestService.emailTemplatePutRequest.bind(this.emailTemplatesRequestService)
  );

  constructor(
    private actions$: Actions,
    private emailTemplatesRequestService: EmailTemplatesRequestService
  ) {
  }
}
