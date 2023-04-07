import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { RepresentativesRequestService } from '../request.service';

export const feeScheduleGetRequestHandler = createRequestHandler('Get Fee Schedule Request');

export function feeScheduleRequestReducer(state, action) {
  return feeScheduleGetRequestHandler.reducer(state, action);
}

@Injectable()
export class FeeScheduleGetRequestEffects {

  @Effect()
  feeSchedule$: Observable<Action> = feeScheduleGetRequestHandler.effect(
    this.actions$,
    this.representativesRequestService.feeScheduleGetRequest.bind(this.representativesRequestService)
  );

  constructor(
    private actions$: Actions,
    private representativesRequestService: RepresentativesRequestService,
  ) {
  }
}
