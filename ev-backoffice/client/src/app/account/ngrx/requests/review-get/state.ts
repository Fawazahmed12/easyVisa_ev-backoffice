import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const reviewGetRequestHandler = createRequestHandler('GetReviewRequest');

export function reviewGetRequestReducer(state, action) {
  return reviewGetRequestHandler.reducer(state, action);
}

@Injectable()
export class ReviewGetRequestEffects {

  @Effect()
  review$: Observable<Action> = reviewGetRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.reviewGetRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
