import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { FeeScheduleRequestService } from '../request.service';


export const feeScheduleSettingsPostRequestHandler = createRequestHandler('Fee Schedule Settings Post');

export function feeScheduleSettingsPostRequestReducer(state, action) {
  return feeScheduleSettingsPostRequestHandler.reducer(state, action);
}

@Injectable()
export class FeeScheduleSettingsPostRequestEffects {

  @Effect()
  feeScheduleSettingsPost$: Observable<Action> = feeScheduleSettingsPostRequestHandler.effect(
    this.actions$,
    this.feeScheduleRequestService.feeScheduleSettingsPostRequest.bind(this.feeScheduleRequestService)
  );

  constructor(
    private actions$: Actions,
    private feeScheduleRequestService: FeeScheduleRequestService
  ) {
  }
}
