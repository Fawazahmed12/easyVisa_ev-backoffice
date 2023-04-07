import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';
import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';

import {
  getActiveReview,
  getActiveReviewId,
  getCreateReviewPostRequestState,
  getReviewGetRequestState,
  getReviewPutRequestState
} from '../ngrx/state';
import { GetReview, PostReview, PutReview, SelectReviewId } from '../ngrx/reviews/reviews.actions';

import { Review } from '../models/review.model';


@Injectable()
export class WriteReviewService {
  activeReviewId$: Observable<number>;
  activeReview$: Observable<Review>;
  createReviewPostState$: Observable<RequestState<any>>;
  reviewGetState$: Observable<RequestState<Review>>;
  reviewPutState$: Observable<RequestState<Review>>;

  constructor(
    private store: Store<State>
  ) {
    this.createReviewPostState$ = this.store.pipe(select(getCreateReviewPostRequestState));
    this.reviewGetState$ = this.store.pipe(select(getReviewGetRequestState));
    this.reviewPutState$ = this.store.pipe(select(getReviewPutRequestState));
    this.activeReviewId$ = this.store.pipe(select(getActiveReviewId));
    this.activeReview$ = this.store.pipe(select(getActiveReview));
  }

  createReview(data) {
    this.store.dispatch(new PostReview(data));
    return this.createReviewPostState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getReview(data) {
    this.store.dispatch(new GetReview(data));
    return this.reviewGetState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updateReview(data) {
    this.store.dispatch(new PutReview(data));
    return this.reviewPutState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  selectReview(reviewId) {
    this.store.dispatch(new SelectReviewId(reviewId));
  }
}
