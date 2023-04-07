import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { UscisEditionDatesRequestService } from '../request.service';

export const uscisEditionDatesPutRequestHandler = createRequestHandler('Uscis Edition Dates Put');

export function uscisEditionDatesPutRequestReducer(state, action) {
  return uscisEditionDatesPutRequestHandler.reducer(state, action);
}

@Injectable()
export class UscisEditionDatesPutRequestEffects {

  @Effect()
  uscisEditionDatesPut$: Observable<Action> = uscisEditionDatesPutRequestHandler.effect(
    this.actions$,
    this.uscisEditionDatesRequestService.uscisEditionDatesPutRequest.bind(this.uscisEditionDatesRequestService)
  );

  constructor(
    private actions$: Actions,
    private uscisEditionDatesRequestService: UscisEditionDatesRequestService
  ) {
  }
}
