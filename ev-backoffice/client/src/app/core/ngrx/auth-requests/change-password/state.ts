import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { AuthRequestService } from '../request.service';

export const changePasswordPutRequestHandler = createRequestHandler('Change Password Put');

export function changePasswordPutRequestReducer(state, action) {
  return changePasswordPutRequestHandler.reducer(state, action);
}


@Injectable()
export class ChangePasswordPutRequestEffects {

  @Effect()
  changePasswordPut$: Observable<Action> = changePasswordPutRequestHandler.effect(
    this.actions$,
    this.authRequestService.changePasswordPutRequest.bind(this.authRequestService)
  );


  constructor(
    private actions$: Actions,
    private authRequestService: AuthRequestService,
  ) {
  }
}
