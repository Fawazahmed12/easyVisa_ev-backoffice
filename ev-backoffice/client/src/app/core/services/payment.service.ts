import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { State } from '../ngrx/state';
import { PaymentMethodDetails } from '../models/payment-method-details.model';
import {
  selectAccountTransactionPostState,
  selectAccountTransactionsGetState,
  selectBalanceGetState,
  selectPayBalancePostState,
  selectPaymentMethodGetState,
  selectPaymentMethodPutState
} from '../ngrx/payment-requests/state';
import { RequestState } from '../ngrx/utils';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import {
  getAccountTransactions,
  getBalance,
  getRepresentativeBalance,
  getPaymentMethod,
  getAccountTransactionsTotal, getBalanceWithEstTaxes
} from '../ngrx/payment/payment.state';
import {
  GetAccountTransactions,
  GetBalance,
  GetPaymentMethod, GetRepresentativeBalance,
  PostAccountTransaction,
  PostPayBalance,
  PutPaymentMethod
} from '../ngrx/payment/payment.actions';
import { AccountTransaction } from '../models/accountTransaction.model';
import { EstimatedTax } from '../models/estimated-tax.model';


@Injectable()
export class PaymentService {

  paymentMethodPutRequest$: Observable<RequestState<PaymentMethodDetails>>;
  paymentMethodGetRequest$: Observable<RequestState<PaymentMethodDetails>>;
  balanceGetRequest$: Observable<RequestState<number>>;
  payBalancePostRequest$: Observable<RequestState<any>>;
  accountTransactionsGetRequest$: Observable<RequestState<AccountTransaction[]>>;
  accountTransactionPostRequest$: Observable<RequestState<AccountTransaction>>;
  balance$: Observable<number>;
  balanceWithEstTaxes$: Observable<EstimatedTax>;
  representativeBalance$: Observable<number>;
  paymentMethod$: Observable<PaymentMethodDetails>;
  accountTransactions$: Observable<AccountTransaction[]>;
  accountTransactionsTotal$: Observable<number>;

  constructor(
    private store: Store<State>
  ) {
    this.paymentMethodPutRequest$ = this.store.pipe(select(selectPaymentMethodPutState));
    this.paymentMethodGetRequest$ = this.store.pipe(select(selectPaymentMethodGetState));
    this.balanceGetRequest$ = this.store.pipe(select(selectBalanceGetState));
    this.payBalancePostRequest$ = this.store.pipe(select(selectPayBalancePostState));
    this.accountTransactionsGetRequest$ = this.store.pipe(select(selectAccountTransactionsGetState));
    this.accountTransactionPostRequest$ = this.store.pipe(select(selectAccountTransactionPostState));
    this.balance$ = this.store.pipe(select(getBalance));
    this.balanceWithEstTaxes$ = this.store.pipe(select(getBalanceWithEstTaxes));
    this.representativeBalance$ = this.store.pipe(select(getRepresentativeBalance));
    this.paymentMethod$ = this.store.pipe(select(getPaymentMethod));
    this.accountTransactions$ = this.store.pipe(select(getAccountTransactions));
    this.accountTransactionsTotal$ = this.store.pipe(select(getAccountTransactionsTotal));
  }

  putPaymentMethod(params) {
    this.store.dispatch(new PutPaymentMethod(params));
    return this.paymentMethodPutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getPaymentMethod(params) {
    this.store.dispatch(new GetPaymentMethod(params));
    return this.paymentMethodGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getMyBalance(params) {
    return this.getBalance(new GetBalance(params));
  }

  getRepresentativeBalance(params) {
    return this.getBalance(new GetRepresentativeBalance(params));
  }

  getBalance(action: GetBalance | GetRepresentativeBalance) {
    this.store.dispatch(action);
    return this.balanceGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getAccountTransactions(params) {
    this.store.dispatch(new GetAccountTransactions(params));
    return this.accountTransactionsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  postAccountTransaction(params) {
    this.store.dispatch(new PostAccountTransaction(params));
    return this.accountTransactionPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  postPayBalance(params) {
    this.store.dispatch(new PostPayBalance(params));
    return this.payBalancePostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  payBalance(data) {
    this.store.dispatch(new PostPayBalance(data));
  }
 }
