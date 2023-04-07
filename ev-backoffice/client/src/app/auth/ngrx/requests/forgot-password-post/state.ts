import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { AuthModuleRequestService } from '../request.service';

export const forgotPasswordPostRequestHandler = createRequestHandler('PostForgotPasswordRequest');

export function forgotPasswordPostRequestReducer(state, action) {
  return forgotPasswordPostRequestHandler.reducer(state, action);
}

@Injectable()
export class ForgotPasswordPostRequestEffects {

  @Effect()
  forgotPassword$: Observable<Action> = forgotPasswordPostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.forgotPasswordPostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
