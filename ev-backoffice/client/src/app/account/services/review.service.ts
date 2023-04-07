import { Injectable } from '@angular/core';
import { select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';
import { Dictionary } from '@ngrx/entity';

import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';
import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';

import {
  getActiveReview,
  getActiveReviewId,
  getCreateReviewPostRequestState,
  getRatingsGetRequestState,
  getReviewGetRequestState,
  getReviewPatchRequestState,
  getReviewPutRequestState,
  getReviews,
  getReviewsEntities,
  getReviewsGetRequestState,
  getReviewsRatings,
  getReviewsTotal,
  getReviewsTotalFiltered,
} from '../ngrx/state';
import { GetRatings, GetReview, GetReviews, PatchReview, PostReview, PutReview, SelectReviewId } from '../ngrx/reviews/reviews.actions';

import { Review } from '../models/review.model';


@Injectable()
export class ReviewService {
  activeReviewId$: Observable<number>;
  activeReview$: Observable<Review>;
  totalReviews$: Observable<number>;
  totalFilteredReviews$: Observable<number>;
  reviewsRating$: Observable<any>;
  createReviewPostState$: Observable<RequestState<any>>;
  reviewGetState$: Observable<RequestState<Review>>;
  ratingsGetState$: Observable<RequestState<any>>;
  reviewsGetState$: Observable<RequestState<Review[]>>;
  reviewPutState$: Observable<RequestState<Review>>;
  reviewPatchState$: Observable<RequestState<Review>>;
  reviewsEntities$: Observable<Dictionary<Review>>;
  reviews$: Observable<Review[]>;

  constructor(
    private store: Store<State>
  ) {
    this.createReviewPostState$ = this.store.pipe(select(getCreateReviewPostRequestState));
    this.ratingsGetState$ = this.store.pipe(select(getRatingsGetRequestState));
    this.reviewGetState$ = this.store.pipe(select(getReviewGetRequestState));
    this.reviewsGetState$ = this.store.pipe(select(getReviewsGetRequestState));
    this.reviewPutState$ = this.store.pipe(select(getReviewPutRequestState));
    this.reviewPatchState$ = this.store.pipe(select(getReviewPatchRequestState));
    this.activeReviewId$ = this.store.pipe(select(getActiveReviewId));
    this.activeReview$ = this.store.pipe(select(getActiveReview));
    this.totalReviews$ = this.store.pipe(select(getReviewsTotal));
    this.totalFilteredReviews$ = this.store.pipe(select(getReviewsTotalFiltered));
    this.reviewsRating$ = this.store.pipe(select(getReviewsRatings));
    this.reviewsEntities$ = this.store.pipe(select(getReviewsEntities));
    this.reviews$ = this.store.pipe(select(getReviews));
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

  getReviews(params) {
    this.store.dispatch(new GetReviews(params));
    return this.reviewsGetState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getRatings(id) {
    this.store.dispatch(new GetRatings(id));
    return this.ratingsGetState$.pipe(
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

  patchReview(data) {
    this.store.dispatch(new PatchReview(data));
    return this.reviewPatchState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  selectReview(reviewId) {
    this.store.dispatch(new SelectReviewId(reviewId));
  }
}
