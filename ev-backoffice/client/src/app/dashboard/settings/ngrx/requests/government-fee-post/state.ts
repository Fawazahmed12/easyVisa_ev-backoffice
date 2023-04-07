import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { map, pluck } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';
import { GovernmentFee } from '../../../../../core/models/government-fee.model';
import { SetGovernmentFee } from '../../../../../core/ngrx/config-data/config-data.actions';

import { DashboardSettingsRequestService } from '../request.service';

export const governmentFeePostRequestHandler = createRequestHandler('PostGovernmentFeeRequest');

export function governmentFeePostRequestReducer(state, action) {
  return governmentFeePostRequestHandler.reducer(state, action);
}

@Injectable()
export class GovernmentFeePostRequestEffects {

  @Effect()
  governmentFeePost$: Observable<Action> = governmentFeePostRequestHandler.effect(
    this.actions$,
    this.dashboardSettingsRequestService.governmentFeePost.bind(this.dashboardSettingsRequestService)
  );

  @Effect()
  setGovernmentFee$: Observable<Action> = this.actions$.pipe(
    ofType(governmentFeePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    pluck('payload'),
    map((payload: GovernmentFee) => new SetGovernmentFee(payload))
  );

  constructor(
    private actions$: Actions,
    private dashboardSettingsRequestService: DashboardSettingsRequestService,
  ) {
  }
}
