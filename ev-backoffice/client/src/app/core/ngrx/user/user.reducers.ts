import {createEntityAdapter, EntityAdapter} from '@ngrx/entity';

import {User} from '../../models/user.model';

import {PaymentActionTypes, PostPayBalanceSuccess, PutPaymentMethodSuccess} from '../payment/payment.actions';
import {AuthActionTypes} from '../auth/auth.actions';

import {
  CancelMembershipSuccess,
  GetUser,
  GetUserIdByEVIdSuccess,
  GetUserSuccess,
  LoginInModalSuccess,
  PatchLoggedInAttorneySuccess,
  SetRegistrationRepresentativeType,
  UpdateUserProfile,
  UserActionsUnion,
  UserActionTypes
} from './user.actions';
import {UserState} from './user.state';

export const adapter: EntityAdapter<any> = createEntityAdapter<any>();

export const initialState: UserState = adapter.getInitialState({
  currentUserId: null,
  currentUserToken: null,
  loggedIn: null,
  registrationRepresentativeType: null,
  findUserId: null,
});

export function reducer(state = initialState, action: UserActionsUnion) {
  switch (action.type) {

    case UserActionTypes.GetUser: {

      const token = (action as GetUser).payload;

      return {
        ...state,
        currentUserToken: token,
        loggedIn: null,
      };
    }

    case UserActionTypes.GetUserIdByEVIdSuccess: {

      const id = (action as GetUserIdByEVIdSuccess).payload.id;

      return {
        ...state,
        findUserId: id,
      };
    }

    case UserActionTypes.GetUserIdByEVIdFailure: {

      return {
        ...state,
        findUserId: null,
      };
    }

    case UserActionTypes.GetUserSuccess: {

      const user = (action as GetUserSuccess).payload;

      return {
        ...adapter.upsertOne(user, state),
        currentUserId: user.id,
        loggedIn: true,
      };
    }

    case UserActionTypes.ChangeUser:
    case UserActionTypes.CancelMembershipSuccess:
    case UserActionTypes.ReActivateMembershipSuccess: {

      const user = (action as CancelMembershipSuccess).payload;

      return {
        ...adapter.updateOne({id: user.id, changes: user}, state),
      };
    }

    case UserActionTypes.LoginInModal: {
      return {
        ...state,
        loggedIn: false,
      };
    }

    case UserActionTypes.LoginInModalSuccess:
    case AuthActionTypes.PutPasswordSuccess: {
      return {
        ...state,
        currentUserToken: (action as LoginInModalSuccess).payload.access_token,
        loggedIn: true,
      };
    }

    case UserActionTypes.LogoutSuccess:
    case UserActionTypes.LogoutFailure:
    case UserActionTypes.DeleteUserSuccess: {

      return {
        ...adapter.removeOne(state.currentUserId, state),
        currentUserId: null,
        currentUserToken: null,
        loggedIn: false,
      };
    }

    case UserActionTypes.PreLoginInModal:
    case UserActionTypes.GetUserFailure:
    case UserActionTypes.GetTokenFailure: {
      return {
        ...state,
        currentUserId: null,
        currentUserToken: null,
        loggedIn: false,
        registrationAttorneyType: null
      };
    }


    case UserActionTypes.PatchLoggedInAttorneySuccess: {
      return {
        ...state,
        ...adapter.upsertOne((action as PatchLoggedInAttorneySuccess).payload, state),
      };
    }

    case UserActionTypes.SetRegistrationRepresentativeType: {
      return {
        ...state,
        registrationRepresentativeType: (action as SetRegistrationRepresentativeType).payload
      };
    }

    case UserActionTypes.UpdateUserProfile: {
      const currentUser: User = state.entities[state.currentUserId];
      const updatedUser = {
        ...currentUser,
        profile: (action as UpdateUserProfile).payload,
      };
      return {
        ...state,
        ...adapter.upsertOne(updatedUser, state),
      };
    }

    case PaymentActionTypes.PutPaymentMethodSuccess:
    case PaymentActionTypes.PostPayBalanceSuccess: {
      const currentUser: User = state.entities[state.currentUserId];
      const payload: any = (action as PutPaymentMethodSuccess | PostPayBalanceSuccess).payload;
      const balance = payload.balance ? payload.balance.subTotal : payload.subTotal;
      const updatedUser = {
        ...currentUser,
        paid: balance >= 0,
        profile: {...currentUser.profile, balance},
      };
      return {
        ...state,
        ...adapter.updateOne({id: state.currentUserId, changes: updatedUser}, state),
      };
    }

    default: {
      return state;
    }
  }
}
