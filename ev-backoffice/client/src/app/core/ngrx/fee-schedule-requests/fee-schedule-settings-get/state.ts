import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { FeeScheduleRequestService } from '../request.service';


export const feeScheduleSettingsGetRequestHandler = createRequestHandler('Fee Schedule Settings Get');

export function feeScheduleSettingsGetRequestReducer(state, action) {
  return feeScheduleSettingsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class FeeScheduleSettingsGetRequestEffects {

  @Effect()
  feeScheduleSettingsGet$: Observable<Action> = feeScheduleSettingsGetRequestHandler.effect(
    this.actions$,
    this.feeScheduleRequestService.feeScheduleSettingsGetRequest.bind(this.feeScheduleRequestService)
  );

  constructor(
    private actions$: Actions,
    private feeScheduleRequestService: FeeScheduleRequestService
  ) {
  }
}
