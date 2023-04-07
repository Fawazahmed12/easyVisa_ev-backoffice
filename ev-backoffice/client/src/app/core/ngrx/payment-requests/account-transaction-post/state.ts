import { Action } from '@ngrx/store';
import { Injectable } from '@angular/core';

import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { PaymentRequestService } from '../request.service';

export const accountTransactionPostRequestHandler = createRequestHandler('Account Transaction Post');

export function accountTransactionPostRequestReducer(state, action) {
  return accountTransactionPostRequestHandler.reducer(state, action);
}


@Injectable()
export class AccountTransactionPostRequestEffects {

  @Effect()
  accountTransactionPost$: Observable<Action> = accountTransactionPostRequestHandler.effect(
    this.actions$,
    this.paymentRequestService.accountTransactionPostRequest.bind(this.paymentRequestService)
  );

  constructor(
    private actions$: Actions,
    private paymentRequestService: PaymentRequestService
  ) {
  }
}
