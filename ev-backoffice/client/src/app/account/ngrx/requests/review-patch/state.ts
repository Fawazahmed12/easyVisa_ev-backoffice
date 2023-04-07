import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { AccountModuleRequestService } from '../request.service';

export const reviewPatchRequestHandler = createRequestHandler('PatchReviewRequest');

export function reviewPatchRequestReducer(state, action) {
  return reviewPatchRequestHandler.reducer(state, action);
}

@Injectable()
export class ReviewPatchRequestEffects {

  @Effect()
  review$: Observable<Action> = reviewPatchRequestHandler.effect(
    this.actions$,
    this.accountModuleRequestService.reviewPatchRequest.bind(this.accountModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private accountModuleRequestService: AccountModuleRequestService,
  ) {
  }
}
