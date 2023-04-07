import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const profilePutRequestHandler = createRequestHandler('PutProfileRequest');

export function profilePutRequestReducer(state, action) {
  return profilePutRequestHandler.reducer(state, action);
}

@Injectable()
export class ProfilePutRequestEffects {

  @Effect()
  profile$: Observable<Action> = profilePutRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.profilePutRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
