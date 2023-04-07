import { InviteRequestState } from './invite-request.state';
import {
  InviteRequestActionsUnion,
  InviteRequestActionTypes,
  PutInviteSuccess, PutRequestSuccess,
  SetInvite, SetRequest
} from './invite-request.action';

export const initialState: InviteRequestState = {
  invite: null,
  request: null,
};

export function reducer(state = initialState, action: InviteRequestActionsUnion) {
  switch (action.type) {

    case InviteRequestActionTypes.PutInvite:
    case InviteRequestActionTypes.DeleteInviteSuccess: {
      return {
        ...state,
        invite: null,
      };
    }

    case InviteRequestActionTypes.PutInviteSuccess:
    case InviteRequestActionTypes.SetInvite: {
      return {
        ...state,
        invite: (action as PutInviteSuccess | SetInvite).payload,
      };
    }

    case InviteRequestActionTypes.PutRequest:
    case InviteRequestActionTypes.DeleteRequestSuccess: {
      return {
        ...state,
        request: null,
      };
    }

    case InviteRequestActionTypes.PutRequestSuccess:
    case InviteRequestActionTypes.SetRequest: {
      return {
        ...state,
        request: (action as PutRequestSuccess | SetRequest).payload,
      };
    }

    default: {
      return state;
    }
  }
}
