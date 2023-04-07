import {
  GetRatingsSuccess,
  GetReviewsSuccess,
  GetReviewSuccess, PatchReviewSuccess, PostReviewSuccess, PutReviewSuccess,
  ReviewsActionsUnion, ReviewsActionTypes, SelectReviewId,
} from './reviews.actions';
import { adapter, ReviewsState } from './reviews.state';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

export const initialState: ReviewsState = adapter.getInitialState({
  activeReviewId: null,
  total: null,
  ratings: null,
  totalFiltered: null,
});

export function reducer(state = initialState, action: ReviewsActionsUnion) {
  switch (action.type) {

    case ReviewsActionTypes.GetReview: {
      return {
        ...state,
        activeReviewId: null
      };
    }

    case ReviewsActionTypes.GetReviewSuccess: {
      return {
        ...adapter.addOne((action as GetReviewSuccess).payload, state),
        activeReviewId: (action as GetReviewSuccess).payload.id
      };
    }

    case ReviewsActionTypes.GetReviewsSuccess: {
      const {body, xTotalCount} = action.payload;
      return {
        ...adapter.setAll(body, state),
        totalFiltered: parseInt(xTotalCount, 10),
      };
    }

    case ReviewsActionTypes.GetRatingsSuccess: {
      return {
        ...state,
        total: (action as GetRatingsSuccess).payload.total,
        ratings: (action as GetRatingsSuccess).payload.ratings,
      };
    }

    case ReviewsActionTypes.PostReviewSuccess: {
      return {
        ...adapter.addOne((action as PostReviewSuccess).payload, state),
        activeReviewId: (action as PostReviewSuccess).payload.id
      };
    }

    case ReviewsActionTypes.PatchReviewSuccess:
    case ReviewsActionTypes.PutReviewSuccess: {
      return {
        ...state,
        ...adapter.upsertOne((action as PutReviewSuccess | PatchReviewSuccess).payload, state),
      };
    }

    case ReviewsActionTypes.SelectReviewId: {
      return {
        ...state,
        activeReviewId: (action as SelectReviewId).payload,
      };
    }

    default: {
      return state;
    }
  }
}
