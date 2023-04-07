import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AuthModuleRequestService } from '../request.service';

export const signUpInfoGetRequestHandler = createRequestHandler('SignUpInfoGet');

export function signUpInfoGetRequestReducer(state, action) {
  return signUpInfoGetRequestHandler.reducer(state, action);
}

@Injectable()
export class SignUpInfoGetRequestEffects {

  @Effect()
  signUpInfo$: Observable<Action> = signUpInfoGetRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.signUpInfoGetRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
