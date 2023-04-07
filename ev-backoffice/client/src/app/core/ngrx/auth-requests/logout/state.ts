import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { AuthRequestService } from '../request.service';

export const logoutRequestHandler = createRequestHandler('Logout');

export function logoutRequestReducer(state, action) {
  return logoutRequestHandler.reducer(state, action);
}


@Injectable()
export class LogoutRequestEffects {

  @Effect()
  logOut$: Observable<Action> = logoutRequestHandler.effect(
    this.actions$,
    this.authRequestService.logoutRequest.bind(this.authRequestService)
  );


  constructor(
    private actions$: Actions,
    private authRequestService: AuthRequestService,
  ) {
  }
}
