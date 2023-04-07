import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';

import { DashboardSettingsRequestService } from '../request.service';


export const batchJobsGetRequestHandler = createRequestHandler('GetBatchJobs');

export function batchJobsGetRequestReducer(state, action) {
  return batchJobsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class BatchJobsGetRequestEffects {

  @Effect()
  batchJobsGet$: Observable<Action> = batchJobsGetRequestHandler.effect(
    this.actions$,
    this.dashboardSettingsRequestService.batchJobsGet.bind(this.dashboardSettingsRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardSettingsRequestService: DashboardSettingsRequestService,
  ) {
  }
}
