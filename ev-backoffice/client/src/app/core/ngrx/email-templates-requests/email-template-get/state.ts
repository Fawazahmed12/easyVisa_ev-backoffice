import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { EmailTemplatesRequestService } from '../request.service';

export const emailTemplateGetRequestHandler = createRequestHandler('Email Template Get');

export function emailTemplateGetRequestReducer(state, action) {
  return emailTemplateGetRequestHandler.reducer(state, action);
}

@Injectable()
export class EmailTemplateGetRequestEffects {

  @Effect()
  emailTemplateGet$: Observable<Action> = emailTemplateGetRequestHandler.effect(
    this.actions$,
    this.emailTemplatesRequestService.emailTemplateGetRequest.bind(this.emailTemplatesRequestService)
  );

  constructor(
    private actions$: Actions,
    private emailTemplatesRequestService: EmailTemplatesRequestService
  ) {
  }
}
