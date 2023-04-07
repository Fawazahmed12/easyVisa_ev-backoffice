import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';

import { DashboardSettingsRequestService } from '../request.service';


export const representativesCountGetRequestHandler = createRequestHandler('GetRepresentativesCount');

export function representativesCountGetRequestReducer(state, action) {
  return representativesCountGetRequestHandler.reducer(state, action);
}

@Injectable()
export class RepresentativesCountGetRequestEffects {

  @Effect()
  representativesCountGet$: Observable<Action> = representativesCountGetRequestHandler.effect(
    this.actions$,
    this.dashboardSettingsRequestService.representativesCountGet.bind(this.dashboardSettingsRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardSettingsRequestService: DashboardSettingsRequestService,
  ) {
  }
}
