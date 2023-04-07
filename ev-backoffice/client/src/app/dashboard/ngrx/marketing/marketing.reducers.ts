import { MarketingState } from './marketing.state';
import { GetMarketingDetailsSuccess, MarketingActionsUnion, MarketingActionTypes } from './marketing.actions';

export const initialState: MarketingState = {
  marketingDetails: null,
};

export function reducer(state = initialState, action: MarketingActionsUnion) {
  switch (action.type) {

    case MarketingActionTypes.GetMarketingDetailsSuccess: {
      return {
        ...state,
        marketingDetails: (action as GetMarketingDetailsSuccess).payload
      };
    }

    case MarketingActionTypes.GetMarketingDetailsFailure: {
      return {
        ...state,
        marketingDetails: null
      };
    }

    default: {
      return state;
    }
  }
}
