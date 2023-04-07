import { TaxTypes } from '../../models/tax-types.enum';

import { PostEstimatedTaxSuccess, TaxesActionTypes, TaxesActionUnion } from './taxes.actions';


export const initialState = {
  sighUpFee: null,
  reactivationFee: null,
  packageChangingStatusFee: null,
};

export function reducer(state = initialState, action: TaxesActionUnion) {

  switch (action.type) {
    case TaxesActionTypes.PostEstimatedTaxSuccess: {
      const payload = (action as PostEstimatedTaxSuccess).payload;
      return PostEstimatedTax(state, payload);
    }

    default: {
      return state;
    }
  }
}

function PostEstimatedTax(state, payload) {
  const feeType = payload.taxType;
  const feeValue = payload.estimatedTax;

  return {
    ...state,
    sighUpFee: feeType === TaxTypes.SIGNUP_FEE ? feeValue : state.sighUpFee,
    reactivationFee: feeType === TaxTypes.MEMBERSHIP_REACTIVATION_FEE ? feeValue : state.reactivationFee,
    packageChangingStatusFee: feeType === TaxTypes.IMMIGRATION_BENEFIT ? feeValue : state.packageChangingStatusFee,
  };
}
