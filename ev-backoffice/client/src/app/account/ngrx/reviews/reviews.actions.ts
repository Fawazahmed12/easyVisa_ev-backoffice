import { HttpResponse } from '@angular/common/http';

import { Action } from '@ngrx/store';

import { Review } from '../../models/review.model';

import { REVIEWS } from './reviews.state';

export const ReviewsActionTypes = {
  GetReview: `[${REVIEWS}] Get Review`,
  GetReviewSuccess: `[${REVIEWS}] Get Review Success`,
  GetReviewFailure: `[${REVIEWS}] Get Review Failure`,
  GetReviews: `[${REVIEWS}] Get Reviews`,
  GetReviewsSuccess: `[${REVIEWS}] Get Reviews Success`,
  GetReviewsFailure: `[${REVIEWS}] Get Reviews Failure`,
  GetRatings: `[${REVIEWS}] Get Ratings`,
  GetRatingsSuccess: `[${REVIEWS}] Get Ratings Success`,
  GetRatingsFailure: `[${REVIEWS}] Get Ratings Failure`,
  PostReview: `[${REVIEWS}] Post Review`,
  PostReviewSuccess: `[${REVIEWS}] Post Review Success`,
  PostReviewFailure: `[${REVIEWS}] Post Review Failure`,
  PutReview: `[${REVIEWS}] Put Review`,
  PutReviewSuccess: `[${REVIEWS}] Put Review Success`,
  PutReviewFailure: `[${REVIEWS}] Put Review Failure`,
  PatchReview: `[${REVIEWS}] Patch Review`,
  PatchReviewSuccess: `[${REVIEWS}] Patch Review Success`,
  PatchReviewFailure: `[${REVIEWS}] Patch Review Failure`,
  SelectReviewId: `[${REVIEWS}] Select Review Id`,
};

export class SelectReviewId implements Action {
  readonly type = ReviewsActionTypes.SelectReviewId;

  constructor(public payload: number) {
  }
}

export class GetReview implements Action {
  readonly type = ReviewsActionTypes.GetReview;

  constructor(public payload?: any) {
  }
}

export class GetReviewSuccess implements Action {
  readonly type = ReviewsActionTypes.GetReviewSuccess;

  constructor(public payload: Review) {
  }
}

export class GetReviewFailure implements Action {
  readonly type = ReviewsActionTypes.GetReviewFailure;

  constructor(public payload?: any) {
  }
}

export class GetReviews implements Action {
  readonly type = ReviewsActionTypes.GetReviews;

  constructor(public payload?: any) {
  }
}

export class GetReviewsSuccess implements Action {
  readonly type = ReviewsActionTypes.GetReviewsSuccess;

  constructor(public payload: {body: Review[]; xTotalCount: string}) {
  }
}

export class GetReviewsFailure implements Action {
  readonly type = ReviewsActionTypes.GetReviewsFailure;

  constructor(public payload?: any) {
  }
}

export class GetRatings implements Action {
  readonly type = ReviewsActionTypes.GetRatings;

  constructor(public payload?: any) {
  }
}

export class GetRatingsSuccess implements Action {
  readonly type = ReviewsActionTypes.GetRatingsSuccess;

  constructor(public payload: any) {
  }
}

export class GetRatingsFailure implements Action {
  readonly type = ReviewsActionTypes.GetRatingsFailure;

  constructor(public payload?: any) {
  }
}

export class PostReview implements Action {
  readonly type = ReviewsActionTypes.PostReview;

  constructor(public payload: Review) {
  }
}

export class PostReviewSuccess implements Action {
  readonly type = ReviewsActionTypes.PostReviewSuccess;

  constructor(public payload: Review) {
  }
}

export class PostReviewFailure implements Action {
  readonly type = ReviewsActionTypes.PostReviewFailure;

  constructor(public payload?: any) {
  }
}

export class PutReview implements Action {
  readonly type = ReviewsActionTypes.PutReview;

  constructor(public payload: Review) {
  }
}

export class PutReviewSuccess implements Action {
  readonly type = ReviewsActionTypes.PutReviewSuccess;

  constructor(public payload: Review) {
  }
}

export class PutReviewFailure implements Action {
  readonly type = ReviewsActionTypes.PutReviewFailure;

  constructor(public payload?: any) {
  }
}

export class PatchReview implements Action {
  readonly type = ReviewsActionTypes.PatchReview;

  constructor(public payload: Review) {
  }
}

export class PatchReviewSuccess implements Action {
  readonly type = ReviewsActionTypes.PatchReviewSuccess;

  constructor(public payload: Review) {
  }
}

export class PatchReviewFailure implements Action {
  readonly type = ReviewsActionTypes.PatchReviewFailure;

  constructor(public payload?: any) {
  }
}

export type ReviewsActionsUnion =
  | GetReview
  | GetReviewSuccess
  | GetReviewFailure
  | GetReviews
  | GetReviewsSuccess
  | GetReviewsFailure
  | GetRatings
  | GetRatingsSuccess
  | GetRatingsFailure
  | PostReview
  | PostReviewSuccess
  | PostReviewFailure
  | PutReview
  | PutReviewSuccess
  | PutReviewFailure
  | PatchReview
  | PatchReviewSuccess
  | PatchReviewFailure
  | SelectReviewId;
