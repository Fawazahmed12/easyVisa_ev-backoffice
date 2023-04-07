import { TaxesRequestState } from './state';
import { estimatedTaxPostRequestReducer } from './post-estimated-tax/state';

export function reducer(state: TaxesRequestState = {}, action): TaxesRequestState {
  return {
    estimatedTaxPost: estimatedTaxPostRequestReducer(state.estimatedTaxPost, action),
  };
}
