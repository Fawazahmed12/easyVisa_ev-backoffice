import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { EmailTemplatesRequestService } from '../request.service';

export const defaultEmailTemplateGetRequestHandler = createRequestHandler('Default Email Template Get');

export function defaultEmailTemplateGetRequestReducer(state, action) {
  return defaultEmailTemplateGetRequestHandler.reducer(state, action);
}

@Injectable()
export class DefaultEmailTemplateGetRequestEffects {

  @Effect()
  defaultEmailTemplateGet$: Observable<Action> = defaultEmailTemplateGetRequestHandler.effect(
    this.actions$,
    this.emailTemplatesRequestService.defaultEmailTemplateGetRequest.bind(this.emailTemplatesRequestService)
  );

  constructor(
    private actions$: Actions,
    private emailTemplatesRequestService: EmailTemplatesRequestService
  ) {
  }
}
