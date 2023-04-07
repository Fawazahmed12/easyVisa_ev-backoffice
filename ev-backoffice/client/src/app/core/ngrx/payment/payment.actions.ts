import { Action } from '@ngrx/store';
import { HttpResponse } from '@angular/common/http';

import { PaymentMethodDetails } from '../../models/payment-method-details.model';
import { AccountTransaction } from '../../models/accountTransaction.model';

import { PAYMENT } from './payment.state';
import { EstimatedTax } from '../../models/estimated-tax.model';

export const PaymentActionTypes = {
  GetBalance: `[${PAYMENT}] Get Balance`,
  GetBalanceSuccess: `[${PAYMENT}] Get Balance Success`,
  GetBalanceFailure: `[${PAYMENT}] Get Balance Failure`,
  GetRepresentativeBalance: `[${PAYMENT}] Get Representative Balance`,
  UpdateRepresentativeBalanceAfterCreateCredit: `[${PAYMENT}] Update Representative Balance After Create Credit`,
  GetRepresentativeBalanceSuccess: `[${PAYMENT}] Representative Get Balance Success`,
  GetRepresentativeBalanceFailure: `[${PAYMENT}] Get Representative Balance Failure`,
  GetAccountTransactions: `[${PAYMENT}] Get Account Transactions`,
  GetAccountTransactionsSuccess: `[${PAYMENT}] Get Account Transactions Success`,
  GetAccountTransactionsFailure: `[${PAYMENT}] Get Account Transactions Failure`,
  PostAccountTransaction: `[${PAYMENT}] Post Account Transaction`,
  PostAccountTransactionSuccess: `[${PAYMENT}] Post Account Transaction Success`,
  PostAccountTransactionFailure: `[${PAYMENT}] Post Account Transaction Failure`,
  GetPaymentMethod: `[${PAYMENT}] Get Payment Method`,
  GetPaymentMethodSuccess: `[${PAYMENT}] Get Payment Method Success`,
  GetPaymentMethodFailure: `[${PAYMENT}] Get Payment Method Failure`,
  PutPaymentMethod: `[${PAYMENT}] Put Payment Method`,
  PutPaymentMethodSuccess: `[${PAYMENT}] Put Payment Method Success`,
  PutPaymentMethodFailure: `[${PAYMENT}] Put Payment Method Failure`,
  PostPayBalance: `[${PAYMENT}] Post Pay Balance`,
  PostPayBalanceSuccess: `[${PAYMENT}] Post Pay Balance Success`,
  PostPayBalanceFailure: `[${PAYMENT}] Post Pay Balance Failure`,
  OpenPackageChargeModal: `[${PAYMENT}] Open Package Charge Modal`,
  OpenPackageChargeFailModal: `[${PAYMENT}] Open Package Charge Fail Modal`,
};

export class GetBalance implements Action {
  readonly type = PaymentActionTypes.GetBalance;

  constructor(public payload: any) {
  }
}

export class GetBalanceSuccess implements Action {
  readonly type = PaymentActionTypes.GetBalanceSuccess;

  constructor(public payload: EstimatedTax) {
  }
}

export class GetBalanceFailure implements Action {
  readonly type = PaymentActionTypes.GetBalanceFailure;

  constructor(public payload: any) {
  }
}

export class GetRepresentativeBalance implements Action {
  readonly type = PaymentActionTypes.GetRepresentativeBalance;

  constructor(public payload: number) {
  }
}

export class UpdateRepresentativeBalanceAfterCreateCredit implements Action {
  readonly type = PaymentActionTypes.UpdateRepresentativeBalanceAfterCreateCredit;

  constructor(public payload: { accountTransaction: AccountTransaction; balance: number }) {
  }
}

export class GetRepresentativeBalanceSuccess implements Action {
  readonly type = PaymentActionTypes.GetRepresentativeBalanceSuccess;

  constructor(public payload: EstimatedTax) {
  }
}

export class GetRepresentativeBalanceFailure implements Action {
  readonly type = PaymentActionTypes.GetRepresentativeBalanceFailure;

  constructor(public payload: any) {
  }
}

export class GetAccountTransactions implements Action {
  readonly type = PaymentActionTypes.GetAccountTransactions;

  constructor(public payload: any) {
  }
}

export class GetAccountTransactionsSuccess implements Action {
  readonly type = PaymentActionTypes.GetAccountTransactionsSuccess;

  constructor(public payload: HttpResponse<any>) {
  }
}

export class GetAccountTransactionsFailure implements Action {
  readonly type = PaymentActionTypes.GetAccountTransactionsFailure;

  constructor(public payload: any) {
  }
}

export class PostAccountTransaction implements Action {
  readonly type = PaymentActionTypes.PostAccountTransaction;

  constructor(public payload: any) {
  }
}

export class PostAccountTransactionSuccess implements Action {
  readonly type = PaymentActionTypes.PostAccountTransactionSuccess;

  constructor(public payload: {accountTransaction: AccountTransaction; balance: string}) {
  }
}

export class PostAccountTransactionFailure implements Action {
  readonly type = PaymentActionTypes.PostAccountTransactionFailure;

  constructor(public payload: any) {
  }
}

export class GetPaymentMethod implements Action {
  readonly type = PaymentActionTypes.GetPaymentMethod;

  constructor(public payload: any) {
  }
}

export class GetPaymentMethodSuccess implements Action {
  readonly type = PaymentActionTypes.GetPaymentMethodSuccess;

  constructor(public payload: PaymentMethodDetails) {
  }
}

export class GetPaymentMethodFailure implements Action {
  readonly type = PaymentActionTypes.GetPaymentMethodFailure;

  constructor(public payload: any) {
  }
}

export class PutPaymentMethod implements Action {
  readonly type = PaymentActionTypes.PutPaymentMethod;

  constructor(public payload: any) {
  }
}

export class PutPaymentMethodSuccess implements Action {
  readonly type = PaymentActionTypes.PutPaymentMethodSuccess;

  constructor(
    public payload:
      {
        paymentMethod: PaymentMethodDetails;
        message: string;
        balance: EstimatedTax;
      }
  ) {
  }
}

export class PutPaymentMethodFailure implements Action {
  readonly type = PaymentActionTypes.PutPaymentMethodFailure;

  constructor(public payload: any) {
  }
}

export class PostPayBalance implements Action {
  readonly type = PaymentActionTypes.PostPayBalance;

  constructor(public payload: any) {
  }
}

export class PostPayBalanceSuccess implements Action {
  readonly type = PaymentActionTypes.PostPayBalanceSuccess;

  constructor(public payload: EstimatedTax) {
  }
}

export class PostPayBalanceFailure implements Action {
  readonly type = PaymentActionTypes.PostPayBalanceFailure;

  constructor(public payload: any) {
  }
}

export class OpenPackageChargeModal implements Action {
  readonly type = PaymentActionTypes.OpenPackageChargeModal;

  constructor(public payload: any) {
  }
}

export class OpenPackageChargeFailModal implements Action {
  readonly type = PaymentActionTypes.OpenPackageChargeFailModal;

  constructor(public payload: any) {
  }
}


export type PaymentActionsUnion =
  | GetBalance
  | GetBalanceSuccess
  | GetBalanceFailure
  | GetRepresentativeBalance
  | UpdateRepresentativeBalanceAfterCreateCredit
  | GetRepresentativeBalanceSuccess
  | GetRepresentativeBalanceFailure
  | GetAccountTransactions
  | GetAccountTransactionsSuccess
  | GetAccountTransactionsFailure
  | PostAccountTransaction
  | PostAccountTransactionSuccess
  | PostAccountTransactionFailure
  | GetPaymentMethod
  | GetPaymentMethodSuccess
  | GetPaymentMethodFailure
  | PutPaymentMethod
  | PutPaymentMethodSuccess
  | PutPaymentMethodFailure
  | PostPayBalance
  | PostPayBalanceSuccess
  | PostPayBalanceFailure
  | OpenPackageChargeModal
  | OpenPackageChargeFailModal;
