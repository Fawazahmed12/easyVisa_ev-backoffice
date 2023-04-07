import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { EmailTemplatesRequestService } from '../request.service';

export const emailTemplateVariablesGetRequestHandler = createRequestHandler('Email Template Variables Get');

export function emailTemplateVariablesGetRequestReducer(state, action) {
  return emailTemplateVariablesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class EmailTemplateVariablesGetRequestEffects {

  @Effect()
  emailTemplateVariablesGet$: Observable<Action> = emailTemplateVariablesGetRequestHandler.effect(
    this.actions$,
    this.emailTemplatesRequestService.emailTemplateVariablesGetRequest.bind(this.emailTemplatesRequestService)
  );

  constructor(
    private actions$: Actions,
    private emailTemplatesRequestService: EmailTemplatesRequestService
  ) {
  }
}
