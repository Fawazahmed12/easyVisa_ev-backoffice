import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { PaymentRequestService } from '../request.service';

export const accountTransactionsGetRequestHandler = createRequestHandler('Account Transactions Get');

export function accountTransactionsGetRequestReducer(state, action) {
  return accountTransactionsGetRequestHandler.reducer(state, action);
}


@Injectable()
export class AccountTransactionsGetRequestEffects {

  @Effect()
  accountTransactionsGet$: Observable<Action> = accountTransactionsGetRequestHandler.effect(
    this.actions$,
    this.paymentRequestService.accountTransactionsGetRequest.bind(this.paymentRequestService)
  );

  constructor(
    private actions$: Actions,
    private paymentRequestService: PaymentRequestService
  ) {
  }
}
