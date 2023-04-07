import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { AuthRequestService } from '../request.service';

export const loginInModalRequestHandler = createRequestHandler('Login In Modal');

export function loginInModalRequestReducer(state, action) {
  return loginInModalRequestHandler.reducer(state, action);
}


@Injectable()
export class LoginInModalRequestEffects {

  @Effect()
  userGetSelfData$: Observable<Action> = loginInModalRequestHandler.effect(
    this.actions$,
    this.authRequestService.loginRequest.bind(this.authRequestService)
  );


  constructor(
    private actions$: Actions,
    private authRequestService: AuthRequestService,
  ) {
  }
}
