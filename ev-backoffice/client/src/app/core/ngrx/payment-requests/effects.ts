import { PaymentMethodPutRequestEffects } from './payment-method-put/state';
import { PaymentMethodGetRequestEffects } from './payment-method-get/state';
import { BalanceGetRequestEffects } from './balance-get/state';
import { PayBalancePostRequestEffects } from './pay-balance-post/state';
import { AccountTransactionsGetRequestEffects } from './account-transactions-get/state';
import { AccountTransactionPostRequestEffects } from './account-transaction-post/state';

export const PaymentRequestEffects = [
  PaymentMethodPutRequestEffects,
  PaymentMethodGetRequestEffects,
  BalanceGetRequestEffects,
  PayBalancePostRequestEffects,
  AccountTransactionsGetRequestEffects,
  AccountTransactionPostRequestEffects,
];
