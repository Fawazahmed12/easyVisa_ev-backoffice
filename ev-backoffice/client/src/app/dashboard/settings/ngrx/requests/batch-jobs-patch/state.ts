import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';

import { DashboardSettingsRequestService } from '../request.service';


export const batchJobsPatchRequestHandler = createRequestHandler('PatchBatchJobs');

export function batchJobsPatchRequestReducer(state, action) {
  return batchJobsPatchRequestHandler.reducer(state, action);
}

@Injectable()
export class BatchJobsPatchRequestEffects {

  @Effect()
  batchJobsPatch$: Observable<Action> = batchJobsPatchRequestHandler.effect(
    this.actions$,
    this.dashboardSettingsRequestService.batchJobsPatch.bind(this.dashboardSettingsRequestService)
  );

  constructor(
    private actions$: Actions,
    private dashboardSettingsRequestService: DashboardSettingsRequestService,
  ) {
  }
}
