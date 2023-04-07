import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { MilestoneDatesRequestService } from '../request.service';


export const  milestoneDatePostRequestHandler = createRequestHandler('Milestone Date Post');

export function  milestoneDatePostRequestReducer(state, action) {
  return  milestoneDatePostRequestHandler.reducer(state, action);
}

@Injectable()
export class MilestoneDatePostRequestEffects {

  @Effect()
  milestoneDatePost$: Observable<Action> =  milestoneDatePostRequestHandler.effect(
    this.actions$,
    this.milestoneDatesRequestService.milestoneDatePostRequest.bind(this.milestoneDatesRequestService)
  );

  constructor(
    private actions$: Actions,
    private milestoneDatesRequestService: MilestoneDatesRequestService
  ) {
  }
}
