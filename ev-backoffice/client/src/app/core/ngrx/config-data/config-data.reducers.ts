import {
  ConfigDataActionsUnion,
  ConfigDataActionTypes, GetBenefitCategoriesSuccess,
  GetFeeDetailsSuccess, GetGovernmentFeeSuccess,
  SetFeeDetails,
} from './config-data.actions';

export const initialState = {
  feeDetails: null,
  governmentFee: null,
  benefits: null,
};

export function reducer(state = initialState, action: ConfigDataActionsUnion) {
  switch (action.type) {

    case ConfigDataActionTypes.SetFeeDetails:
    case ConfigDataActionTypes.PostFeeDetailsSuccess:
    case ConfigDataActionTypes.GetFeeDetailsSuccess: {
      return {
        ...state,
        feeDetails: (action as GetFeeDetailsSuccess | SetFeeDetails).payload,
      };
    }

    case ConfigDataActionTypes.GetFeeDetailsFailure: {
      return {
        ...state,
        feeDetails: null,
      };
    }

    case ConfigDataActionTypes.GetGovernmentFeeFailure: {
      return {
        ...state,
        governmentFee: null,
      };
    }

    case ConfigDataActionTypes.GetGovernmentFeeSuccess:
    case ConfigDataActionTypes.SetGovernmentFee: {
      return {
        ...state,
        governmentFee: (action as GetGovernmentFeeSuccess).payload,
      };
    }

    case ConfigDataActionTypes.GetBenefitCategoriesSuccess: {
      return {
        ...state,
        benefits: (action as GetBenefitCategoriesSuccess).payload,
      };
    }

    default: {
      return state;
    }
  }
}
