import { Injectable } from '@angular/core';

import { EMPTY, Observable } from 'rxjs';
import { catchError, map, pluck, switchMap, tap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { FeeDetails } from '../../models/fee-details.model';

import { RequestFailAction, RequestSuccessAction } from '../utils';
import { feeDetailsGetRequestHandler } from '../config-data-requests/fee-details-get/state';
import { governmentFeeGetRequestHandler } from '../config-data-requests/government-fee-get/state';

import {
  ConfigDataActionTypes,
  GetBenefitCategoriesSuccess,
  GetFeeDetailsSuccess,
  GetGovernmentFeeSuccess, PostFeeDetails, PostFeeDetailsFailure,
  PostFeeDetailsSuccess
} from './config-data.actions';
import { benefitCategories } from '../../models/benefit-categories';
import { benefitsGetRequestHandler } from '../config-data-requests/benefits-get/state';
import { Benefits } from '../../models/benefits.model';
import { feeDetailsPostRequestHandler } from '../config-data-requests/fee-details-post/state';
import { ModalService } from '../../services';
import { OkButton } from '../../modals/confirm-modal/confirm-modal.component';

@Injectable()
export class ConfigDataEffects {

  @Effect()
  getFeeDetailsData$: Observable<Action> = this.actions$.pipe(
    ofType(ConfigDataActionTypes.GetFeeDetails),
    map(() => feeDetailsGetRequestHandler.requestAction())
  );

  @Effect()
  getFeeDetailsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(feeDetailsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<FeeDetails>) => new GetFeeDetailsSuccess(payload))
  );

  @Effect({dispatch: false})
  getFeeDetailsFail$: Observable<Action> = this.actions$.pipe(
    ofType(feeDetailsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  feeDetailsPost$: Observable<Action> = this.actions$.pipe(
    ofType(ConfigDataActionTypes.PostFeeDetails),
    map(({payload}: PostFeeDetails) => feeDetailsPostRequestHandler.requestAction(payload))
  );

  @Effect()
  feeDetailsPostSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(feeDetailsPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    pluck('payload'),
    map((payload: FeeDetails) => new PostFeeDetailsSuccess(payload))
  );

  @Effect({ dispatch: false })
  feeDetailsPostSuccessModal$: Observable<Action> = this.actions$.pipe(
    ofType(feeDetailsPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    switchMap(() =>
      this.modalService.openConfirmModal({
        header: 'TEMPLATE.DASHBOARD.SETTINGS.CUSTOMER_FEES.UPDATE_SUCCESS_TITLE',
        body: 'TEMPLATE.DASHBOARD.SETTINGS.CUSTOMER_FEES.UPDATE_FEES_SUCCESS_BODY',
        centered: true,
        buttons: [OkButton],
      }).pipe(
        catchError(() => EMPTY)
      )
    ),
  );

  @Effect()
  feeDetailsPostFailure$: Observable<Action> = this.actions$.pipe(
    ofType(feeDetailsPostRequestHandler.ActionTypes.REQUEST_FAIL),
    pluck('payload'),
    map((payload: FeeDetails) => new PostFeeDetailsFailure(payload))
  );

  @Effect()
  getGovernmentFee$: Observable<Action> = this.actions$.pipe(
    ofType(ConfigDataActionTypes.GetGovernmentFee),
    map(() => governmentFeeGetRequestHandler.requestAction())
  );

  @Effect()
  getGovernmentFeeSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(governmentFeeGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<FeeDetails>) => new GetGovernmentFeeSuccess(payload))
  );

  @Effect({dispatch: false})
  getGovernmentFeeFail$: Observable<Action> = this.actions$.pipe(
    ofType(governmentFeeGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  getBenefits$: Observable<Action> = this.actions$.pipe(
    ofType(ConfigDataActionTypes.GetBenefitCategories),
    map(() => benefitsGetRequestHandler.requestAction())
  );

  @Effect()
  getBenefitsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(benefitsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Benefits>) => new GetBenefitCategoriesSuccess(payload))
  );

  @Effect({dispatch: false})
  getBenefitsFail$: Observable<Action> = this.actions$.pipe(
    ofType(benefitsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
