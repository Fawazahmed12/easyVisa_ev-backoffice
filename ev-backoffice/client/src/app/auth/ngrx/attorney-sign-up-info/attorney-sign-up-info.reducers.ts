import {
  AttorneySignUpInfoActionsUnion,
  AttorneySignUpInfoActionTypes,
} from './attorney-sign-up-info.actions';

export const initialState = {
  referringUserName: null,
  referralEmail: null,
  attorneySignUpInfo: null,
};

export function reducer(state = initialState, action: AttorneySignUpInfoActionsUnion) {
  switch (action.type) {
    case AttorneySignUpInfoActionTypes.GetReferringUserSuccess: {
      return {
        ...state,
        referringUserName: action.payload,
      };
    }

    case AttorneySignUpInfoActionTypes.PostAttorneyUserSuccess: {
      return {
        ...state,
        attorneySignUpInfo: action.payload,
      };
    }

    case AttorneySignUpInfoActionTypes.PostAddReferralSuccess: {
      return {
        ...state,
        referralEmail: action.payload.email,
      };
    }

    default: {
      return state;
    }
  }
}
