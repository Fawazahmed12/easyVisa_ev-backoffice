import { Injectable } from '@angular/core';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';

import { filter, map, pluck, switchMap, tap, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { PackageModalType } from '../../../task-queue/models/package-modal-type.enum';

import { PaymentMethodDetails } from '../../models/payment-method-details.model';
import { ModalService } from '../../services';
import { OkButton } from '../../modals/confirm-modal/confirm-modal.component';
import { AccountTransaction } from '../../models/accountTransaction.model';

import { RequestFailAction, RequestSuccessAction } from '../utils';
import { balanceGetRequestHandler } from '../payment-requests/balance-get/state';
import { payBalancePostRequestHandler } from '../payment-requests/pay-balance-post/state';
import { paymentMethodGetRequestHandler } from '../payment-requests/payment-method-get/state';
import { paymentMethodPutRequestHandler } from '../payment-requests/payment-method-put/state';
import { GetUserIdByEVIdSuccess, UserActionTypes } from '../user/user.actions';
import { accountTransactionsGetRequestHandler } from '../payment-requests/account-transactions-get/state';
import { accountTransactionPostRequestHandler } from '../payment-requests/account-transaction-post/state';
import { State } from '../state';

import {
  GetAccountTransactions,
  GetAccountTransactionsFailure,
  GetAccountTransactionsSuccess,
  GetBalance,
  GetBalanceFailure,
  GetBalanceSuccess,
  GetPaymentMethod,
  GetPaymentMethodSuccess,
  GetRepresentativeBalance,
  GetRepresentativeBalanceSuccess,
  OpenPackageChargeFailModal,
  OpenPackageChargeModal,
  PaymentActionTypes,
  PostAccountTransaction,
  PostAccountTransactionSuccess,
  PostPayBalance,
  PostPayBalanceSuccess,
  PutPaymentMethod,
  PutPaymentMethodSuccess,
  UpdateRepresentativeBalanceAfterCreateCredit
} from './payment.actions';
import { EstimatedTax } from '../../models/estimated-tax.model';
import { getFindUserId, getIsCurrentRepresentativeMe } from '../user/user.state';
import {
  getCurrentRepresentative,
  getCurrentRepresentativeUserId
} from '../representatives/representatives.state';


@Injectable()
export class PaymentEffects {

  @Effect()
  getBalance$: Observable<Action> = this.actions$.pipe(
    ofType(
      PaymentActionTypes.GetBalance,
      PaymentActionTypes.GetRepresentativeBalance,
    ),
    map((payload: GetBalance) => balanceGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getBalancesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(balanceGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    pluck('payload'),
    map((payload: {actionType: string; balance: EstimatedTax}) => {
      switch (payload.actionType) {
        case PaymentActionTypes.GetBalance: {
          return new GetBalanceSuccess(payload.balance);
        }
        case PaymentActionTypes.GetRepresentativeBalance: {
          return new GetRepresentativeBalanceSuccess(payload.balance);
        }
        default: {
          return  new GetBalanceFailure(payload);
        }
      }
    })
  );

  @Effect({dispatch: false})
  getBalancesFail$: Observable<Action> = this.actions$.pipe(
    ofType(balanceGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );


  @Effect()
  getBalanceAccountTransactionsAfterGetUserId$: Observable<Action> = this.actions$.pipe(
    ofType(UserActionTypes.GetUserIdByEVIdSuccess),
    switchMap(({payload}: GetUserIdByEVIdSuccess) => [
      new GetRepresentativeBalance(payload.id),
      new GetAccountTransactions({id: payload.id}),
    ])
  );

  @Effect()
  getAccountTransactions$: Observable<Action> = this.actions$.pipe(
    ofType(PaymentActionTypes.GetAccountTransactions),
    map(({payload}: GetAccountTransactions) => accountTransactionsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getAccountTransactionsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(accountTransactionsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<AccountTransaction[]>) => new GetAccountTransactionsSuccess(payload))
  );

  @Effect()
  getAccountTransactionsFail$: Observable<Action> = this.actions$.pipe(
    ofType(accountTransactionsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new GetAccountTransactionsFailure(payload))
  );

  @Effect()
  postAccountTransaction$: Observable<Action> = this.actions$.pipe(
    ofType(PaymentActionTypes.PostAccountTransaction),
    map(({payload}: PostAccountTransaction) => accountTransactionPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postAccountTransactionSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(accountTransactionPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    withLatestFrom(this.store.pipe(select(getFindUserId))),
    switchMap(([{payload}, id]: [RequestSuccessAction<AccountTransaction>, number]) => [
      new PostAccountTransactionSuccess(payload),
      new UpdateRepresentativeBalanceAfterCreateCredit(payload),
      new GetAccountTransactions({id})
    ])
  );

  @Effect({dispatch: false})
  postAccountTransactionFail$: Observable<Action> = this.actions$.pipe(
    ofType(accountTransactionPostRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  postPayBalance$: Observable<Action> = this.actions$.pipe(
    ofType(PaymentActionTypes.PostPayBalance),
    map(({payload}: PostPayBalance) => payBalancePostRequestHandler.requestAction(payload))
  );

  @Effect()
  postPayBalanceSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(payBalancePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<any>) => new PostPayBalanceSuccess(payload))
  );

  @Effect()
  postPayBalanceFail$: Observable<Action> = this.actions$.pipe(
    ofType(payBalancePostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new OpenPackageChargeFailModal(payload))
  );

  @Effect()
  getAccountTransactionsAfterPayBalance$: Observable<Action> = this.actions$.pipe(
    ofType(PaymentActionTypes.PostPayBalanceSuccess),
    withLatestFrom(this.store.pipe(select(getCurrentRepresentativeUserId))),
    map(([, id]) => new GetAccountTransactions({id}))
  );

  @Effect()
  getPaymentMethod$: Observable<Action> = this.actions$.pipe(
    ofType(PaymentActionTypes.GetPaymentMethod),
    map(({payload}: GetPaymentMethod) => paymentMethodGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getPaymentMethodSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(paymentMethodGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<PaymentMethodDetails>) => new GetPaymentMethodSuccess(payload))
  );

  @Effect({dispatch: false})
  getPaymentMethodFail$: Observable<Action> = this.actions$.pipe(
    ofType(paymentMethodGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  putPaymentMethod$: Observable<Action> = this.actions$.pipe(
    ofType(PaymentActionTypes.PutPaymentMethod),
    map(({payload}: PutPaymentMethod) => paymentMethodPutRequestHandler.requestAction(payload))
  );

  @Effect()
  putPaymentMethodSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(paymentMethodPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    switchMap(({payload}: RequestSuccessAction<PaymentMethodDetails>) => [
      new PutPaymentMethodSuccess(payload),
      new OpenPackageChargeModal(payload.message),
    ])
  );

  @Effect()
  getTransactionsAfterPutPaymentMethodSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(PaymentActionTypes.PutPaymentMethod),
    withLatestFrom(
      this.store.pipe(select(getCurrentRepresentative)),
      this.store.pipe(select(getIsCurrentRepresentativeMe)),
    ),
    filter(([, currentRep, isMe]) => isMe),
    map(([, currentRep, isMe]) => currentRep.userId),
    map((id) => new GetAccountTransactions({id}),
    )
  );

  @Effect({dispatch: false})
  putPaymentMethodFail$: Observable<Action> = this.actions$.pipe(
    ofType(
      paymentMethodPutRequestHandler.ActionTypes.REQUEST_FAIL,
      PaymentActionTypes.GetAccountTransactionsFailure,
      PaymentActionTypes.GetBalanceFailure,
      ),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || payload.error))
  );

  @Effect({dispatch: false})
  openPackageChargeModal$: Observable<Action> = this.actions$.pipe(
    ofType(PaymentActionTypes.OpenPackageChargeModal),
    filter(({payload}: RequestFailAction<any>) =>
      !!payload.text && [PackageModalType.PAYMENT_CHARGED, PackageModalType.PAYMENT_FAILED].includes(payload.type)
    ),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.openConfirmModal({
        header: 'TEMPLATE.MODAL.PAYMENT_TITLE',
        body: payload.text,
        buttons: [OkButton],
      }))
  );

  @Effect({dispatch: false})
  openPackageChargeFailModal$: Observable<Action> = this.actions$.pipe(
    ofType(PaymentActionTypes.OpenPackageChargeFailModal),
    switchMap(({payload}: RequestFailAction<any>) => this.modalService.showErrorModal(payload.error.errors || payload.error))
  );


  constructor(
    private actions$: Actions,
    private store: Store<State>,
    private modalService: ModalService,
  ) {
  }
}
