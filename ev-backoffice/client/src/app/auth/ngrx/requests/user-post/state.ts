import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { createRequestHandler, RequestSuccessAction } from '../../../../core/ngrx/utils';
import { PostUserSuccess } from '../../../../core/ngrx/user/user.actions';
import { User } from '../../../../core/models/user.model';
import { AuthModuleRequestService } from '../request.service';

export const userPostRequestHandler = createRequestHandler('UserPostRequest');

export function userPostRequestReducer(state, action) {
  return userPostRequestHandler.reducer(state, action);
}

@Injectable()
export class UserPostRequestEffects {

  @Effect()
  userData$: Observable<Action> = userPostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.userPostRequest.bind(this.authModuleRequestService)
  );

  @Effect()
  createUserSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(userPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<User>) => new PostUserSuccess(payload))
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
