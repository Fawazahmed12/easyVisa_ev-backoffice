import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { AuthModuleRequestService } from '../request.service';

export const completePaymentPostRequestHandler = createRequestHandler('CompletePaymentPostRequest');

export function completePaymentPostRequestReducer(state, action) {
  return completePaymentPostRequestHandler.reducer(state, action);
}

@Injectable()
export class CompletePaymentPostRequestEffects {

  @Effect()
  attorneyData$: Observable<Action> = completePaymentPostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.completePaymentPostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
