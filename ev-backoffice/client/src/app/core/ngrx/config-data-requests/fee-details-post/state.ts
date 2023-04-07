import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { ConfigDataRequestService } from '../../services';

export const feeDetailsPostRequestHandler = createRequestHandler('PostFeeDetailsRequest');

export function feeDetailsPostRequestReducer(state, action) {
  return feeDetailsPostRequestHandler.reducer(state, action);
}

@Injectable()
export class FeeDetailsPostRequestEffects {

  @Effect()
  feeDetailsPost$: Observable<Action> = feeDetailsPostRequestHandler.effect(
    this.actions$,
    this.configDataRequestService.feeDetailsPost.bind(this.configDataRequestService)
  );


  constructor(
    private actions$: Actions,
    private configDataRequestService: ConfigDataRequestService,
  ) {
  }
}
