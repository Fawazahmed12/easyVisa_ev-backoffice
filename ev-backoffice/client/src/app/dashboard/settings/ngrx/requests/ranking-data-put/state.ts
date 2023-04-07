import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';

import { DashboardSettingsRequestService } from '../request.service';


export const rankingDataPutRequestHandler = createRequestHandler('PutRankingData');

export function rankingDataPutRequestReducer(state, action) {
  return rankingDataPutRequestHandler.reducer(state, action);
}

@Injectable()
export class RankingDataPutRequestEffects {

  @Effect()
  rankingDataPut$: Observable<Action> = rankingDataPutRequestHandler.effect(
    this.actions$,
    this.dashboardSettingsRequestService.rankingDataPut.bind(this.dashboardSettingsRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardSettingsRequestService: DashboardSettingsRequestService,
  ) {
  }
}
