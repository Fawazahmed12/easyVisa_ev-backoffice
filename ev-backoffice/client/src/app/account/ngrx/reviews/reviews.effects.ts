import { Injectable } from '@angular/core';

import { Actions, Effect, ofType } from '@ngrx/effects';
import { Action, select, Store } from '@ngrx/store';
import { Dictionary } from '@ngrx/entity';
import { EMPTY, Observable } from 'rxjs';
import { catchError, filter, map, pluck, switchMap, tap, withLatestFrom } from 'rxjs/operators';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';
import { State } from '../../../core/ngrx/state';

import { Review } from '../../models/review.model';

import { reviewGetRequestHandler } from '../requests/review-get/state';
import { createReviewPostRequestHandler } from '../requests/create-review-post/state';
import { reviewPutRequestHandler } from '../requests/review-put/state';
import { reviewsGetRequestHandler } from '../requests/reviews-get/state';
import { ratingsGetRequestHandler } from '../requests/ratings-get/state';
import { reviewPatchRequestHandler } from '../requests/review-patch/state';
import { getReviewsEntities } from '../state';

import {
  GetRatings, GetRatingsSuccess,
  GetReview, GetReviews,
  GetReviewsSuccess,
  GetReviewSuccess, PatchReview, PatchReviewSuccess,
  PostReview, PostReviewFailure,
  PostReviewSuccess, PutReview, PutReviewFailure,
  PutReviewSuccess, ReviewsActionsUnion,
  ReviewsActionTypes, SelectReviewId
} from './reviews.actions';
import { HttpErrorResponse } from '@angular/common/http';
import { ModalService } from '../../../core/services';
import { OkButtonLg } from '../../../core/modals/confirm-modal/confirm-modal.component';


@Injectable()
export class ReviewsEffects {

  @Effect()
  SelectReview$: Observable<any> = this.actions$.pipe(
    ofType(ReviewsActionTypes.SelectReviewId),
    filter((payload) => !!payload),
    withLatestFrom(this.store.pipe(select(getReviewsEntities))),
    filter(([action, reviewsEntities]: [SelectReviewId, Dictionary<Review>]) =>
      reviewsEntities[action.payload] && !reviewsEntities[action.payload].read),
    map(([action, reviewsEntities]: [SelectReviewId, Dictionary<Review>]) =>
      reviewPatchRequestHandler.requestAction({id: action.payload, read: true})
    )
  );

  @Effect()
  GetReview$: Observable<Action> = this.actions$.pipe(
    ofType(ReviewsActionTypes.GetReview),
    map(({payload}: GetReview) => reviewGetRequestHandler.requestAction(payload))
  );

  @Effect()
  GetReviewSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(reviewGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Review[]>) => new GetReviewSuccess(payload))
  );

  @Effect({dispatch: false})
  GetReviewFailure$: Observable<Action> = this.actions$.pipe(
    ofType(reviewGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  GetReviews$: Observable<Action> = this.actions$.pipe(
    ofType(ReviewsActionTypes.GetReviews),
    map(({payload}: GetReviews) => reviewsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  GetReviewsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(reviewsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Review[]>) => new GetReviewsSuccess(payload))
  );

  @Effect({dispatch: false})
  GetReviewsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(reviewsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  GetRatings$: Observable<Action> = this.actions$.pipe(
    ofType(ReviewsActionTypes.GetRatings),
    map(({payload}: GetRatings) => ratingsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  GetRatingsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(ratingsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<any>) => new GetRatingsSuccess(payload))
  );

  @Effect({dispatch: false})
  GetRatingsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(ratingsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  @Effect()
  PostReview$: Observable<Action> = this.actions$.pipe(
    ofType(ReviewsActionTypes.PostReview),
    map(({payload}: PostReview) => createReviewPostRequestHandler.requestAction(payload))
  );

  @Effect()
  PostReviewSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(createReviewPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Review[]>) => new PostReviewSuccess(payload))
  );

  @Effect()
  PostReviewFailure$: Observable<Action> = this.actions$.pipe(
    ofType(createReviewPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PostReviewFailure(payload))
  );

  @Effect({dispatch: false})
  CreateUpdateReviewFailureModal$: Observable<Action> = this.actions$.pipe(
    ofType(ReviewsActionTypes.PostReviewFailure, ReviewsActionTypes.PutReviewFailure),
    pluck('payload'),
    filter((error: HttpErrorResponse) => error && error.status !== 401),
    switchMap((error: HttpErrorResponse) =>
      this.modalService.showErrorModal(error.error?.errors || [error.error])
    )
  );

  @Effect({dispatch: false})
  CreateUpdateReviewSuccessModal$: Observable<Action> = this.actions$.pipe(
    ofType(ReviewsActionTypes.PostReviewSuccess, ReviewsActionTypes.PutReviewSuccess),
    map(({type}) => type === ReviewsActionTypes.PostReviewSuccess ? 'CREATE_BODY' : 'UPDATE_BODY'),
    switchMap((bodyType) => this.modalService.openConfirmModal({
       header: 'TEMPLATE.ACCOUNT.WRITE_A_REVIEW.MODALS.UPSERT_SUCCESS.TITLE',
       body: `TEMPLATE.ACCOUNT.WRITE_A_REVIEW.MODALS.UPSERT_SUCCESS.${bodyType}`,
       buttons: [OkButtonLg],
       centered: true,
     }).pipe(
       catchError(() => EMPTY)
     ))
  );

  @Effect()
  PutReview$: Observable<Action> = this.actions$.pipe(
    ofType(ReviewsActionTypes.PutReview),
    map(({payload}: PutReview) => reviewPutRequestHandler.requestAction(payload))
  );

  @Effect()
  PutReviewSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(reviewPutRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Review[]>) => new PutReviewSuccess(payload))
  );

  @Effect()
  PutReviewFailure$: Observable<Action> = this.actions$.pipe(
    ofType(reviewPutRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({payload}: RequestFailAction<any>) => new PutReviewFailure(payload))
  );

  @Effect()
  PatchReview$: Observable<Action> = this.actions$.pipe(
    ofType(ReviewsActionTypes.PatchReview),
    map(({payload}: PatchReview) => reviewPatchRequestHandler.requestAction(payload))
  );

  @Effect()
  PatchReviewSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(reviewPatchRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({payload}: RequestSuccessAction<Review>) => new PatchReviewSuccess(payload))
  );

  @Effect({dispatch: false})
  PatchReviewFailure$: Observable<Action> = this.actions$.pipe(
    ofType(reviewPatchRequestHandler.ActionTypes.REQUEST_FAIL),
    tap(({payload}: RequestFailAction<any>) => {
      console.log(payload);
    })
  );

  constructor(
    private actions$: Actions<ReviewsActionsUnion>,
    private store: Store<State>,
    private modalService: ModalService,
  ) {
  }

}
