import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { ConfigDataRequestService } from '../request.service';

export const feeDetailsGetRequestHandler = createRequestHandler('Fee Details Get');

export function feeDetailsGetRequestReducer(state, action) {
  return feeDetailsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class FeeDetailsGetRequestEffects {

  @Effect()
  feeDetailsGetSelfData$: Observable<Action> = feeDetailsGetRequestHandler.effect(
    this.actions$,
    this.configDataRequestService.configDataGetRequest.bind(this.configDataRequestService)
  );

  constructor(
    private actions$: Actions,
    private configDataRequestService: ConfigDataRequestService
  ) {
  }
}
