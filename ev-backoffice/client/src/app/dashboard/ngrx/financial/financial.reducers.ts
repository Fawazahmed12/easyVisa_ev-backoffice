import { FinancialState } from './financial.state';
import { FinancialActionsUnion, FinancialActionTypes, GetFinancialDetailsSuccess } from './financial.actions';

export const initialState: FinancialState = {
  financialDetails: null,
};

export function reducer(state = initialState, action: FinancialActionsUnion) {
  switch (action.type) {

    case FinancialActionTypes.GetFinancialDetailsSuccess: {
      return {
        ...state,
        financialDetails: (action as GetFinancialDetailsSuccess).payload
      };
    }

    case FinancialActionTypes.GetFinancialDetailsFailure: {
      return {
        ...state,
        financialDetails: null
      };
    }

    default: {
      return state;
    }
  }
}
