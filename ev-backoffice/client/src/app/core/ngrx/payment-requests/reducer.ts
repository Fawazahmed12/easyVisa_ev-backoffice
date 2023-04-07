import { PaymentRequestState } from './state';
import { paymentMethodPutRequestReducer } from './payment-method-put/state';
import { paymentMethodGetRequestReducer } from './payment-method-get/state';
import { balanceGetRequestReducer } from './balance-get/state';
import { payBalancePostRequestReducer } from './pay-balance-post/state';
import { accountTransactionsGetRequestReducer } from './account-transactions-get/state';
import { accountTransactionPostRequestReducer } from './account-transaction-post/state';

export function reducer(state: PaymentRequestState = {}, action) {
  return {
    paymentMethodPut: paymentMethodPutRequestReducer(state.paymentMethodPut, action),
    paymentMethodGet: paymentMethodGetRequestReducer(state.paymentMethodGet, action),
    balanceGet: balanceGetRequestReducer(state.balanceGet, action),
    payBalancePost: payBalancePostRequestReducer(state.payBalancePost, action),
    accountTransactionsGet: accountTransactionsGetRequestReducer(state.accountTransactionsGet, action),
    accountTransactionPost: accountTransactionPostRequestReducer(state.accountTransactionPost, action),
  };
}
