import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { DashboardRequestService } from '../request.service';


export const financialDetailsGetRequestHandler = createRequestHandler('GetFinancialDetails');

export function financialDetailsGetRequestReducer(state, action) {
  return financialDetailsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class FinancialDetailsGetRequestEffects {

  @Effect()
  financialDetailsGet$: Observable<Action> = financialDetailsGetRequestHandler.effect(
    this.actions$,
    this.dashboardRequestService.financialDetailsGet.bind(this.dashboardRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardRequestService: DashboardRequestService,
  ) {
  }
}
