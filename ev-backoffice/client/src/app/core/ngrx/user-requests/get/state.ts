import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { UserRequestService } from '../request.service';

export const userGetRequestHandler = createRequestHandler('User Get');

export function userGetRequestReducer(state, action) {
  return userGetRequestHandler.reducer(state, action);
}


@Injectable()
export class UserGetRequestEffects {

  @Effect()
  usersGetSelfData$: Observable<Action> = userGetRequestHandler.effect(
    this.actions$,
    this.userRequestService.userGetRequest.bind(this.userRequestService)
  );

  constructor(
    private actions$: Actions,
    private userRequestService: UserRequestService
  ) {
  }
}
