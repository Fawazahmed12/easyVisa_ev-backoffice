import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const reviewPutRequestHandler = createRequestHandler('PutReviewRequest');

export function reviewPutRequestReducer(state, action) {
  return reviewPutRequestHandler.reducer(state, action);
}

@Injectable()
export class ReviewPutRequestEffects {

  @Effect()
  review$: Observable<Action> = reviewPutRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.reviewPutRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
