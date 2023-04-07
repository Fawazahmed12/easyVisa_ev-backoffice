import { Injectable } from '@angular/core';

import { EMPTY, Observable } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';
import { ModalService } from '../../../core/services';

import { MarketingDetails } from '../../models/marketing-details.model';

import { marketingDetailsGetRequestHandler } from '../requests/marketing-details-get/state';

import { GetMarketingDetails, GetMarketingDetailsFailure, GetMarketingDetailsSuccess, MarketingActionTypes } from './marketing.actions';


@Injectable()
export class MarketingEffects {

  @Effect()
  getMarketingDetails$: Observable<Action> = this.actions$.pipe(
    ofType(MarketingActionTypes.GetMarketingDetails),
    map(({payload}: GetMarketingDetails) => marketingDetailsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getMarketingDetailsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(marketingDetailsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<MarketingDetails>) => new GetMarketingDetailsSuccess(payload))
  );

  @Effect()
  getMarketingDetailsFail$: Observable<Action> = this.actions$.pipe(
    ofType(marketingDetailsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new GetMarketingDetailsFailure(payload))
  );

  @Effect({dispatch: false})
  openFailModal$: Observable<Action> = this.actions$.pipe(
    ofType(MarketingActionTypes.GetMarketingDetailsFailure),
    switchMap(({payload}: RequestFailAction<any>) =>
      this.modalService.showErrorModal(payload.error.errors || [payload.error]).pipe(
        catchError(() => EMPTY))
    )
  );


  constructor(
    private actions$: Actions,
    private modalService: ModalService,
  ) {
  }
}
