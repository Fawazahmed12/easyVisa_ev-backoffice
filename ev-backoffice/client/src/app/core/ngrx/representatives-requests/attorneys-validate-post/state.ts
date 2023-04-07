import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../utils';
import { RepresentativesRequestService } from '../request.service';


export const attorneysValidatePostRequestHandler = createRequestHandler('Attorneys Validate Post');

export function attorneysValidatePostRequestReducer(state, action) {
  return attorneysValidatePostRequestHandler.reducer(state, action);
}

@Injectable()
export class AttorneysValidatePostRequestEffects {

  @Effect()
  attorneysValidatePost$: Observable<Action> = attorneysValidatePostRequestHandler.effect(
    this.actions$,
    this.representativesRequestService.attorneysValidatePostRequest.bind(this.representativesRequestService)
  );

  constructor(
    private actions$: Actions,
    private representativesRequestService: RepresentativesRequestService
  ) {
  }
}
