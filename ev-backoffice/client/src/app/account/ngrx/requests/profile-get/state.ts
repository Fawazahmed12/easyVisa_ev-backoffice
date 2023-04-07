import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const profileGetRequestHandler = createRequestHandler('GetProfileRequest');

export function profileGetRequestReducer(state, action) {
  return profileGetRequestHandler.reducer(state, action);
}

@Injectable()
export class ProfileGetRequestEffects {

  @Effect()
  profile$: Observable<Action> = profileGetRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.profileGetRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
