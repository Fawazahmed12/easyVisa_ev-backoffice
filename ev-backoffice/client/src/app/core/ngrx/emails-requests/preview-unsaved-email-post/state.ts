import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { EmailsRequestService } from '../request.service';

export const previewUnsavedEmailPostRequestHandler = createRequestHandler('Preview Unsaved Email Post');

export function previewUnsavedEmailPostRequestReducer(state, action) {
  return previewUnsavedEmailPostRequestHandler.reducer(state, action);
}

@Injectable()
export class PreviewUnsavedEmailPostRequestEffects {

  @Effect()
  previewUnsavedEmail$: Observable<Action> = previewUnsavedEmailPostRequestHandler.effect(
    this.actions$,
    this.emailsRequestService.previewUnsavedEmailPostRequest.bind(this.emailsRequestService)
  );

  constructor(
    private actions$: Actions,
    private emailsRequestService: EmailsRequestService,
  ) {
  }
}
