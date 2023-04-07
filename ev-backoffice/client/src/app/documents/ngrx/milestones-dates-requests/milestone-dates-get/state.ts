import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { MilestoneDatesRequestService } from '../request.service';


export const  milestoneDatesGetRequestHandler = createRequestHandler('Milestone Dates Get');

export function  milestoneDatesGetRequestReducer(state, action) {
  return  milestoneDatesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class MilestoneDatesGetRequestEffects {

  @Effect()
  milestoneDatesGet$: Observable<Action> =  milestoneDatesGetRequestHandler.effect(
    this.actions$,
    this.milestoneDatesRequestService.milestoneDatesGetRequest.bind(this.milestoneDatesRequestService)
  );

  constructor(
    private actions$: Actions,
    private milestoneDatesRequestService: MilestoneDatesRequestService
  ) {
  }
}
