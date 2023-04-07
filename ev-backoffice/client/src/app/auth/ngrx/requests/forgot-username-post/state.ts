import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AuthModuleRequestService } from '../request.service';

export const forgotUsernamePostRequestHandler = createRequestHandler('PostForgotUsernameRequest');

export function forgotUsernamePostRequestReducer(state, action) {
  return forgotUsernamePostRequestHandler.reducer(state, action);
}

@Injectable()
export class ForgotUsernamePostRequestEffects {

  @Effect()
  forgotPassword$: Observable<Action> = forgotUsernamePostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.forgotUsernamePostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
