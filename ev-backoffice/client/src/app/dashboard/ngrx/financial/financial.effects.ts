import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action } from '@ngrx/store';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';

import { FinancialDetails } from '../../models/financial-details.model';

import { financialDetailsGetRequestHandler } from '../requests/financial-details-get/state';

import {
  FinancialActionTypes,
  GetFinancialDetails,
  GetFinancialDetailsFailure,
  GetFinancialDetailsSuccess,
  PostInviteColleagues, PostInviteColleaguesFailure, PostInviteColleaguesSuccess
} from './financial.actions';
import { inviteColleaguesPostRequestHandler } from '../requests/state';


@Injectable()
export class FinancialEffects {

  @Effect()
  getFinancialDetails$: Observable<Action> = this.actions$.pipe(
    ofType(FinancialActionTypes.GetFinancialDetails),
    map(({payload}: GetFinancialDetails) => financialDetailsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getFinancialDetailsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(financialDetailsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<FinancialDetails[]>) => new GetFinancialDetailsSuccess(payload))
  );

  @Effect()
  getFinancialDetailsFail$: Observable<Action> = this.actions$.pipe(
    ofType(financialDetailsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new GetFinancialDetailsFailure(payload))
  );

  @Effect()
  postInviteColleagues$: Observable<Action> = this.actions$.pipe(
    ofType(FinancialActionTypes.PostInviteColleagues),
    map(({payload}: PostInviteColleagues) => inviteColleaguesPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postInviteColleaguesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(inviteColleaguesPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<any>) => new PostInviteColleaguesSuccess(payload))
  );

  @Effect()
  postInviteColleaguesFail$: Observable<Action> = this.actions$.pipe(
    ofType(inviteColleaguesPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PostInviteColleaguesFailure(payload))
  );


  constructor(
    private actions$: Actions,
  ) {
  }

}
