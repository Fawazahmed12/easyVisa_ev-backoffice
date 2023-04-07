import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';

import { DashboardSettingsRequestService } from '../request.service';


export const rankingDataGetRequestHandler = createRequestHandler('GetRankingData');

export function rankingDataGetRequestReducer(state, action) {
  return rankingDataGetRequestHandler.reducer(state, action);
}

@Injectable()
export class RankingDataGetRequestEffects {

  @Effect()
  rankingDataGet$: Observable<Action> = rankingDataGetRequestHandler.effect(
    this.actions$,
    this.dashboardSettingsRequestService.rankingDataGet.bind(this.dashboardSettingsRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardSettingsRequestService: DashboardSettingsRequestService,
  ) {
  }
}
