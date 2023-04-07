import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';


import { AccountModuleRequestService } from '../request.service';

export const createReviewPostRequestHandler = createRequestHandler('PostCreateReviewRequest');

export function createReviewPostRequestReducer(state, action) {
  return createReviewPostRequestHandler.reducer(state, action);
}

@Injectable()
export class CreateReviewPostRequestEffects {

  @Effect()
  createReview$: Observable<Action> = createReviewPostRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.createReviewPostRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
