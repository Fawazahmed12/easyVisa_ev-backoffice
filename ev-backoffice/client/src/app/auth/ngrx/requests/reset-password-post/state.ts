import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AuthModuleRequestService } from '../request.service';

export const resetPasswordPostRequestHandler = createRequestHandler('ResetPasswordPostRequest');

export function resetPasswordPostRequestReducer(state, action) {
  return resetPasswordPostRequestHandler.reducer(state, action);
}

@Injectable()
export class ResetPasswordPostRequestEffects {

  @Effect()
  resetPassword$: Observable<Action> = resetPasswordPostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.resetPasswordPostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
