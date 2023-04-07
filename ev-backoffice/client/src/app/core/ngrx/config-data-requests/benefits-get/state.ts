import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { ConfigDataRequestService } from '../request.service';

export const benefitsGetRequestHandler = createRequestHandler('Benefits Get');

export function benefitsGetRequestReducer(state, action) {
  return benefitsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class BenefitsGetRequestEffects {

  @Effect()
  benefits$: Observable<Action> = benefitsGetRequestHandler.effect(
    this.actions$,
    this.configDataRequestService.benefitsGetRequest.bind(this.configDataRequestService)
  );

  constructor(
    private actions$: Actions,
    private configDataRequestService: ConfigDataRequestService
  ) {
  }
}
