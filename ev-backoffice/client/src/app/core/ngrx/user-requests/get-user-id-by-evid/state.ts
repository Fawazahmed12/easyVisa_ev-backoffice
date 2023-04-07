import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { UserRequestService } from '../request.service';

export const userIdByEVIdGetRequestHandler = createRequestHandler('User Id By EVId Get');

export function userIdByEVIdGetRequestReducer(state, action) {
  return userIdByEVIdGetRequestHandler.reducer(state, action);
}


@Injectable()
export class UserIdByEVIdGetRequestEffects {

  @Effect()
  userIdByEVIdGet$: Observable<Action> = userIdByEVIdGetRequestHandler.effect(
    this.actions$,
    this.userRequestService.userIdByEVIdGetRequest.bind(this.userRequestService)
  );

  constructor(
    private actions$: Actions,
    private userRequestService: UserRequestService
  ) {
  }
}
