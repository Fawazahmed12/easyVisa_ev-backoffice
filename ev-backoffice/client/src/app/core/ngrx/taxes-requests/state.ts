import { createFeatureSelector, createSelector } from '@ngrx/store';

import { RequestState } from '../utils';

import { EstimatedTax } from '../../models/estimated-tax.model';
import { estimatedTaxPostRequestHandler } from './post-estimated-tax/state';

export const TAXES_REQUEST = 'TaxesRequest';

export interface TaxesRequestState {
  estimatedTaxPost?: RequestState<EstimatedTax>;
}

export const selectTaxesRequestState = createFeatureSelector(TAXES_REQUEST);

export const selectEstimatedTaxPostRequestState = createSelector(
  selectTaxesRequestState,
  (state: TaxesRequestState) => state.estimatedTaxPost
);

export { estimatedTaxPostRequestHandler } from './post-estimated-tax/state';

