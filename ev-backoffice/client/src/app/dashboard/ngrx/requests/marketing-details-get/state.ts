import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { DashboardRequestService } from '../request.service';


export const marketingDetailsGetRequestHandler = createRequestHandler('GetMarketingDetails');

export function marketingDetailsGetRequestReducer(state, action) {
  return marketingDetailsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class MarketingDetailsGetRequestEffects {

  @Effect()
  marketingDetailsGet$: Observable<Action> = marketingDetailsGetRequestHandler.effect(
    this.actions$,
    this.dashboardRequestService.marketingDetailsGet.bind(this.dashboardRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardRequestService: DashboardRequestService,
  ) {
  }
}
