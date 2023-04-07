import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { PaymentRequestService } from '../request.service';

export const balanceGetRequestHandler = createRequestHandler('Balance Get');

export function balanceGetRequestReducer(state, action) {
  return balanceGetRequestHandler.reducer(state, action);
}


@Injectable()
export class BalanceGetRequestEffects {

  @Effect()
  balanceGet$: Observable<Action> = balanceGetRequestHandler.effect(
    this.actions$,
    this.paymentRequestService.balanceGetRequest.bind(this.paymentRequestService)
  );

  constructor(
    private actions$: Actions,
    private paymentRequestService: PaymentRequestService
  ) {
  }
}
