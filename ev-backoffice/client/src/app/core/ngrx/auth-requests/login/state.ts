import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { AuthRequestService } from '../request.service';

export const loginRequestHandler = createRequestHandler('Login');

export function loginRequestReducer(state, action) {
  return loginRequestHandler.reducer(state, action);
}


@Injectable()
export class LoginRequestEffects {

  @Effect()
  usersGetSelfData$: Observable<Action> = loginRequestHandler.effect(
    this.actions$,
    this.authRequestService.loginRequest.bind(this.authRequestService)
  );


  constructor(
    private actions$: Actions,
    private authRequestService: AuthRequestService,
  ) {
  }
}
