import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { AuthModuleRequestService } from '../request.service';

export const addReferralPostRequestHandler = createRequestHandler('AddReferralPostRequest');

export function addReferralRequestReducer(state, action) {
  return addReferralPostRequestHandler.reducer(state, action);
}

@Injectable()
export class AddReferralPostRequestEffects {

  @Effect()
  addReferral$: Observable<Action> = addReferralPostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.addReferralPostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
