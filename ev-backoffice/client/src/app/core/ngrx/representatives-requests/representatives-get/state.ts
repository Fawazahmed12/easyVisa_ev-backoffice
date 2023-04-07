import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';
import { RepresentativesRequestService } from '../request.service';


export const representativesGetRequestHandler = createRequestHandler('Representatives Get');

export function representativesGetRequestReducer(state, action) {
  return representativesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class RepresentativesGetRequestEffects {

  @Effect()
  representativesGet$: Observable<Action> = representativesGetRequestHandler.effect(
    this.actions$,
    this.representativesRequestService.representativesGetRequest.bind(this.representativesRequestService)
  );

  constructor(
    private actions$: Actions,
    private representativesRequestService: RepresentativesRequestService
  ) {
  }
}
