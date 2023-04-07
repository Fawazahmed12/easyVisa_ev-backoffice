import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { AuthModuleRequestService } from '../request.service';

export const referringUserGetRequestHandler = createRequestHandler('GetReferringUserRequest');

export function referringUserRequestReducer(state, action) {
  return referringUserGetRequestHandler.reducer(state, action);
}

@Injectable()
export class ReferringUserGetRequestEffects {

  @Effect()
  referringUserData$: Observable<Action> = referringUserGetRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.referringUserRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
