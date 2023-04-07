import { adapter, PaymentState } from './payment.state';
import {
  GetAccountTransactionsSuccess,
  GetBalanceSuccess,
  GetPaymentMethodSuccess,
  GetRepresentativeBalanceSuccess,
  PaymentActionsUnion,
  PaymentActionTypes,
  PostAccountTransactionSuccess,
  PostPayBalanceSuccess,
  PutPaymentMethodSuccess,
  UpdateRepresentativeBalanceAfterCreateCredit,
} from './payment.actions';
import { UserActionTypes } from '../user/user.actions';


export const initialState: PaymentState = adapter.getInitialState({
  balance: null,
  balanceWithEstTaxes: null,
  representativeBalance: null,
  paymentMethod: null,
  total: null,
});

export function reducer(state = initialState, action: PaymentActionsUnion) {
  switch (action.type) {

    case PaymentActionTypes.GetBalanceSuccess: {
      const fullDataBalance = (action as GetBalanceSuccess).payload;
      return {
        ...state,
        balance: -fullDataBalance.subTotal,
        balanceWithEstTaxes: fullDataBalance,
      };
    }

    case PaymentActionTypes.GetRepresentativeBalanceSuccess: {
      const fullDataBalance = (action as GetRepresentativeBalanceSuccess).payload;

      return {
        ...state,
        representativeBalance: fullDataBalance.subTotal,
      };
    }

    case UserActionTypes.GetUserIdByEVIdFailure: {

      return {
        ...state,
        representativeBalance: null,
      };
    }

    case PaymentActionTypes.UpdateRepresentativeBalanceAfterCreateCredit: {
      return {
        ...state,
        representativeBalance: (action as UpdateRepresentativeBalanceAfterCreateCredit).payload.balance,
      };
    }

    case PaymentActionTypes.GetPaymentMethodSuccess: {
      return {
        ...state,
        paymentMethod: (action as GetPaymentMethodSuccess).payload,
      };
    }

    case PaymentActionTypes.PutPaymentMethodSuccess: {
      const payload = (action as PutPaymentMethodSuccess).payload;
      return {
        ...state,
        paymentMethod: payload.paymentMethod,
        balance: -payload.balance.subTotal,
        balanceWithEstTaxes: payload.balance,
      };
    }

    case PaymentActionTypes.GetAccountTransactions: {
      return {
        ...adapter.removeAll(state),
      };
    }

    case PaymentActionTypes.GetAccountTransactionsSuccess: {
      const {body, xTotalCount}: any = (action as GetAccountTransactionsSuccess).payload;
      return {
        ...adapter.setAll(body, state),
        total: parseInt(xTotalCount, 10),
      };
    }

    case PaymentActionTypes.PostAccountTransactionSuccess: {
      return {
        ...adapter.addOne((action as PostAccountTransactionSuccess).payload.accountTransaction, state),
      };
    }

    case PaymentActionTypes.PostPayBalanceSuccess: {
      const payload = (action as PostPayBalanceSuccess).payload;
      return {
        ...state,
        balance: -payload.subTotal,
        balanceWithEstTaxes: payload,
      };
    }

    default: {
      return state;
    }
  }
}
