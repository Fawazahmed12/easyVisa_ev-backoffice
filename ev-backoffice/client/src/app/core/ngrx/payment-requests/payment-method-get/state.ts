import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { PaymentRequestService } from '../request.service';

export const paymentMethodGetRequestHandler = createRequestHandler('Payment Method Get');

export function paymentMethodGetRequestReducer(state, action) {
  return paymentMethodGetRequestHandler.reducer(state, action);
}


@Injectable()
export class PaymentMethodGetRequestEffects {

  @Effect()
  paymentMethodGet$: Observable<Action> = paymentMethodGetRequestHandler.effect(
    this.actions$,
    this.paymentRequestService.paymentMethodGetRequest.bind(this.paymentRequestService)
  );

  constructor(
    private actions$: Actions,
    private paymentRequestService: PaymentRequestService
  ) {
  }
}
