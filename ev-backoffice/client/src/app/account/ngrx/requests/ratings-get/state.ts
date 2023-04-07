import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const ratingsGetRequestHandler = createRequestHandler('GetRatingsRequest');

export function ratingsGetRequestReducer(state, action) {
  return ratingsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class RatingsGetRequestEffects {

  @Effect()
  ratings$: Observable<Action> = ratingsGetRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.ratingsGetRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
