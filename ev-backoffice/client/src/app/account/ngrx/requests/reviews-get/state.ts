import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const reviewsGetRequestHandler = createRequestHandler('GetReviewsRequest');

export function reviewsGetRequestReducer(state, action) {
  return reviewsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class ReviewsGetRequestEffects {

  @Effect()
  reviews$: Observable<Action> = reviewsGetRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.reviewsGetRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
