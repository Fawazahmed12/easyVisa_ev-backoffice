import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';

import { UscisEditionDatesRequestService } from '../request.service';

export const uscisEditionDatesGetRequestHandler = createRequestHandler('Uscis Edition Dates Get');

export function uscisEditionDatesGetRequestReducer(state, action) {
  return uscisEditionDatesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class UscisEditionDatesGetRequestEffects {

  @Effect()
  uscisEditionDatesGet$: Observable<Action> = uscisEditionDatesGetRequestHandler.effect(
    this.actions$,
    this.uscisEditionDatesRequestService.uscisEditionDatesGetRequest.bind(this.uscisEditionDatesRequestService)
  );

  constructor(
    private actions$: Actions,
    private uscisEditionDatesRequestService: UscisEditionDatesRequestService
  ) {
  }
}
