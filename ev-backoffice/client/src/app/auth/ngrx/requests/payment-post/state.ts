import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AuthModuleRequestService } from '../request.service';

export const paymentPostRequestHandler = createRequestHandler('PostPaymentRequest');

export function paymentPostRequestReducer(state, action) {
  return paymentPostRequestHandler.reducer(state, action);
}

@Injectable()
export class PaymentPostRequestEffects {

  @Effect()
  payment$: Observable<Action> = paymentPostRequestHandler.effect(
    this.actions$,
    this.authModuleRequestService.paymentPostRequest.bind(this.authModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private authModuleRequestService: AuthModuleRequestService,
  ) {
  }
}
