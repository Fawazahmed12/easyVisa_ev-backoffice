import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { ConfigDataRequestService } from '../request.service';

export const governmentFeeGetRequestHandler = createRequestHandler('Government Fee Get');

export function governmentFeeGetRequestReducer(state, action) {
  return governmentFeeGetRequestHandler.reducer(state, action);
}

@Injectable()
export class GovernmentFeeGetRequestEffects {

  @Effect()
  governmentFeeGet$: Observable<Action> = governmentFeeGetRequestHandler.effect(
    this.actions$,
    this.configDataRequestService.governmentFeeGetRequest.bind(this.configDataRequestService)
  );

  constructor(
    private actions$: Actions,
    private configDataRequestService: ConfigDataRequestService
  ) {
  }
}
