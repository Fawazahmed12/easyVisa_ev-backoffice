import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../utils';
import { RepresentativesRequestService } from '../request.service';

export const attorneyPatchRequestHandler = createRequestHandler('AttorneyPatchRequest');

export function attorneyPatchRequestReducer(state, action) {
  return attorneyPatchRequestHandler.reducer(state, action);
}

@Injectable()
export class AttorneyPatchRequestEffects {

  @Effect()
  attorneyData$: Observable<Action> = attorneyPatchRequestHandler.effect(
    this.actions$,
    this.representativesRequestService.attorneyPatchRequest.bind(this.representativesRequestService)
  );

  constructor(
    private actions$: Actions,
    private representativesRequestService: RepresentativesRequestService,
  ) {
  }
}
