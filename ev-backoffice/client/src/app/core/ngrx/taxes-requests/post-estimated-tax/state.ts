import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';

import { TaxesRequestService } from '../request.service';

export const estimatedTaxPostRequestHandler = createRequestHandler('Estimated Tax Post');

export function estimatedTaxPostRequestReducer(state, action) {
  return estimatedTaxPostRequestHandler.reducer(state, action);
}


@Injectable()
export class EstimatedTaxPostRequestEffects {

  @Effect()
  estimatedTaxPost$: Observable<Action> = estimatedTaxPostRequestHandler.effect(
    this.actions$,
    this.taxesRequestService.estimatedTaxPostRequest.bind(this.taxesRequestService)
  );


  constructor(
    private actions$: Actions,
    private taxesRequestService: TaxesRequestService,
  ) {
  }
}
