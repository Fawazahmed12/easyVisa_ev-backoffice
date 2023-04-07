import { createFeatureSelector, createSelector } from '@ngrx/store';
import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';

import { PaymentMethodDetails } from '../../models/payment-method-details.model';
import { AccountTransaction } from '../../models/accountTransaction.model';
import { EstimatedTax } from '../../models/estimated-tax.model';

export const PAYMENT = 'Payment';

export interface PaymentState extends EntityState<AccountTransaction> {
  balance: number;
  balanceWithEstTaxes: EstimatedTax;
  representativeBalance: number;
  paymentMethod: PaymentMethodDetails;
  total: number;
}

export const adapter: EntityAdapter<AccountTransaction> = createEntityAdapter<AccountTransaction>();

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectAccountTransactions = selectAll;

export const selectAccountTransactionEntities = selectEntities;

export const selectPaymentState = createFeatureSelector<PaymentState>(PAYMENT);

export const selectBalanceWithEstTaxes = ({balanceWithEstTaxes}: PaymentState) => balanceWithEstTaxes;

export const selectBalance = ({balance}: PaymentState) => balance;

export const selectRepresentativeBalance = ({representativeBalance}: PaymentState) => representativeBalance;

export const selectPaymentMethod = ({paymentMethod}: PaymentState) => paymentMethod;

export const selectAccountTransactionsTotal = ({total}: PaymentState) => total;

export const getAccountTransactions = createSelector(
  selectPaymentState,
  selectAccountTransactions,
);

export const getAccountTransactionEntities = createSelector(
  selectPaymentState,
  selectAccountTransactionEntities,
);

export const getBalance = createSelector(
  selectPaymentState,
  selectBalance,
);

export const getBalanceWithEstTaxes = createSelector(
  selectPaymentState,
  selectBalanceWithEstTaxes,
);

export const getRepresentativeBalance = createSelector(
  selectPaymentState,
  selectRepresentativeBalance,
);

export const getPaymentMethod = createSelector(
  selectPaymentState,
  selectPaymentMethod,
);

export const getAccountTransactionsTotal = createSelector(
  selectPaymentState,
  selectAccountTransactionsTotal,
);
