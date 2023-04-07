import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { UserRequestService } from '../request.service';

export const userDeleteRequestHandler = createRequestHandler('User Delete');

export function userDeleteRequestReducer(state, action) {
  return userDeleteRequestHandler.reducer(state, action);
}


@Injectable()
export class UserDeleteRequestEffects {

  @Effect()
  usersDelete$: Observable<Action> = userDeleteRequestHandler.effect(
    this.actions$,
    this.userRequestService.userDeleteRequest.bind(this.userRequestService)
  );

  constructor(
    private actions$: Actions,
    private userRequestService: UserRequestService
  ) {
  }
}
