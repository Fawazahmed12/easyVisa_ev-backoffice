import { AuthActionTypes, AuthActionUnion, SetRedirectUrl } from './auth.actions';


export const initialState = {
  redirectUrl: null,
};

export function reducer(state = initialState, action: AuthActionUnion) {
  switch (action.type) {
    case AuthActionTypes.SetRedirectUrl: {
      return {
        ...state,
        redirectUrl: (action as SetRedirectUrl).payload,
      };
    }

    case AuthActionTypes.ResetRedirectUrl: {
      return {
        ...state,
        redirectUrl: null,
      };
    }

    default: {
      return state;
    }
  }
}
