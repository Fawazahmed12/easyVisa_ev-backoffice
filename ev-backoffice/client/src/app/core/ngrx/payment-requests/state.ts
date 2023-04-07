import { createFeatureSelector, createSelector } from '@ngrx/store';

import { PaymentMethodDetails } from '../../models/payment-method-details.model';
import { AccountTransaction } from '../../models/accountTransaction.model';

import { RequestState } from '../utils';

import { paymentMethodPutRequestHandler } from './payment-method-put/state';
import { paymentMethodGetRequestHandler } from './payment-method-get/state';

export const PAYMENT_REQUEST = 'PaymentRequest';

export interface PaymentRequestState {
  paymentMethodPut?: RequestState<PaymentMethodDetails>;
  paymentMethodGet?: RequestState<PaymentMethodDetails>;
  balanceGet?: RequestState<number>;
  payBalancePost?: RequestState<any>;
  accountTransactionsGet?: RequestState<AccountTransaction[]>;
  accountTransactionPost?: RequestState<AccountTransaction>;
}

export const selectPaymentRequestState = createFeatureSelector(PAYMENT_REQUEST);

export const selectPaymentMethodPutState = createSelector(
  selectPaymentRequestState,
  (state: PaymentRequestState) => state.paymentMethodPut
);

export const selectPaymentMethodGetState = createSelector(
  selectPaymentRequestState,
  (state: PaymentRequestState) => state.paymentMethodGet
);

export const selectBalanceGetState = createSelector(
  selectPaymentRequestState,
  (state: PaymentRequestState) => state.balanceGet
);

export const selectPayBalancePostState = createSelector(
  selectPaymentRequestState,
  (state: PaymentRequestState) => state.payBalancePost
);

export const selectAccountTransactionsGetState = createSelector(
  selectPaymentRequestState,
  (state: PaymentRequestState) => state.accountTransactionsGet
);

export const selectAccountTransactionPostState = createSelector(
  selectPaymentRequestState,
  (state: PaymentRequestState) => state.accountTransactionPost
);

export { paymentMethodPutRequestHandler } from './payment-method-put/state';
export { paymentMethodGetRequestHandler } from './payment-method-get/state';
export { balanceGetRequestHandler } from './balance-get/state';
export { payBalancePostRequestHandler } from './pay-balance-post/state';
export { accountTransactionsGetRequestHandler } from './account-transactions-get/state';
export { accountTransactionPostRequestHandler } from './account-transaction-post/state';
