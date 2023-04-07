import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { PaymentRequestService } from '../request.service';

export const payBalancePostRequestHandler = createRequestHandler('Pay Balance Post');

export function payBalancePostRequestReducer(state, action) {
  return payBalancePostRequestHandler.reducer(state, action);
}


@Injectable()
export class PayBalancePostRequestEffects {

  @Effect()
  payBalancePost$: Observable<Action> = payBalancePostRequestHandler.effect(
    this.actions$,
    this.paymentRequestService.payBalancePostRequest.bind(this.paymentRequestService)
  );

  constructor(
    private actions$: Actions,
    private paymentRequestService: PaymentRequestService
  ) {
  }
}
