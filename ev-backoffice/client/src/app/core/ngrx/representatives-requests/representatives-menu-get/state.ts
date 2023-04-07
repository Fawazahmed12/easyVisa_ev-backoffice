import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';
import { RepresentativesRequestService } from '../request.service';


export const representativesMenuGetRequestHandler = createRequestHandler('Representatives Get Menu');

export function representativesMenuGetRequestReducer(state, action) {
  return representativesMenuGetRequestHandler.reducer(state, action);
}

@Injectable()
export class RepresentativesMenuGetRequestEffects {

  @Effect()
  representativesMenuGet$: Observable<Action> = representativesMenuGetRequestHandler.effect(
    this.actions$,
    this.representativesRequestService.representativesMenuGetRequest.bind(this.representativesRequestService)
  );

  constructor(
    private actions$: Actions,
    private representativesRequestService: RepresentativesRequestService
  ) {
  }
}
