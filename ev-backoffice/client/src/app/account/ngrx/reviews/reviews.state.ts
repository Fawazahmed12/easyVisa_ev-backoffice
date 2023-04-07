import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { Review } from '../../models/review.model';

export const REVIEWS = 'Reviews';

export interface ReviewsState extends EntityState<Review> {
  activeReviewId: number;
  total: number;
  totalFiltered: number;
  ratings: any[];
}

export const  adapter: EntityAdapter<Review> = createEntityAdapter<Review>();

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectReviews = selectAll;

export const selectReviewsEntities = selectEntities;

export const selectReviewsState = createFeatureSelector<ReviewsState>(REVIEWS);

export const selectActiveReviewId = ({activeReviewId}: ReviewsState) => activeReviewId;

export const selectActiveReview = createSelector(
  selectReviewsEntities,
  selectActiveReviewId,
  (reviewsEntities, reviewId) => reviewsEntities[reviewId]
);

export const selectReviewsTotal = ({total}: ReviewsState) => total;

export const selectReviewsRatings = ({ratings}: ReviewsState) => ratings;

export const selectReviewsTotalFiltered = ({totalFiltered}: ReviewsState) => totalFiltered;
