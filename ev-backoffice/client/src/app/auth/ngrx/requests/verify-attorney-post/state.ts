import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { AuthModuleRequestService } from '../request.service';

export const verifyAttorneyPostRequestHandler = createRequestHandler('PostVerifyAttorneyRequest');

export function verifyAttorneyPostRequestReducer(state, action) {
  return verifyAttorneyPostRequestHandler.reducer(state, action);
}

@Injectable()
export class VerifyAttorneyPostRequestEffects {

  @Effect()
  verifyAttorney$: Observable<Action> = verifyAttorneyPostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.verifyAttorneyPostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
