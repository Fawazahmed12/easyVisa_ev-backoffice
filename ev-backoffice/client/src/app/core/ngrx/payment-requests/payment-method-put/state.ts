import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { PaymentRequestService } from '../request.service';

export const paymentMethodPutRequestHandler = createRequestHandler('Payment Method Put');

export function paymentMethodPutRequestReducer(state, action) {
  return paymentMethodPutRequestHandler.reducer(state, action);
}


@Injectable()
export class PaymentMethodPutRequestEffects {

  @Effect()
  paymentMethodPut$: Observable<Action> = paymentMethodPutRequestHandler.effect(
    this.actions$,
    this.paymentRequestService.paymentMethodPutRequest.bind(this.paymentRequestService)
  );

  constructor(
    private actions$: Actions,
    private paymentRequestService: PaymentRequestService
  ) {
  }
}
