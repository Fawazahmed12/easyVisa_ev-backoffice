import { Action } from '@ngrx/store';

import { User } from '../../models/user.model';
import { LoginModel } from '../../models/login.model';
import { Profile } from '../../models/profile.model';
import { Organization } from '../../models/organization.model';

import { USER } from './user.state';

export const UserActionTypes = {
  Login: `[${USER}] Login`,
  LoginSuccess: `[${USER}] Login Success`,
  LoginFailure: `[${USER}] Login Failure`,
  LoginInModal: `[${USER}] Login In Modal`,
  LoginInModalSuccess: `[${USER}] Login In Modal Success`,
  LoginInModalFailure: `[${USER}] Login In Modal Failure`,
  Logout: `[${USER}] Logout`,
  LogoutSuccess: `[${USER}] Logout Success`,
  LogoutFailure: `[${USER}] Logout Failure`,
  GetUser: `[${USER}] Get User`,
  GetUserSuccess: `[${USER}] Get User Success`,
  GetUserFailure: `[${USER}] Get User Failure`,
  GetUserIdByEVId: `[${USER}] Get User Id By EV Id`,
  GetUserIdByEVIdSuccess: `[${USER}] Get User Id By EV Id Success`,
  GetUserIdByEVIdFailure: `[${USER}] Get User Id By EV Id Failure`,
  GetTokenFailure: `[${USER}] Get Token Failure`,
  PatchAttorneyFailure: `[${USER}] Patch Attorney Failure`,
  PatchLoggedInAttorneySuccess: `[${USER}] Patch LoggedIn Attorney Success`,
  PatchLoggedInAttorneyFailure: `[${USER}] Patch LoggedIn Attorney Failure`,
  PostUserSuccess: `[${USER}] Post User Success`,
  SetRegistrationRepresentativeType: `[${USER}] Set Registration Representative Type`,
  UpdateUserProfile: `[${USER}] Update User Profile`,
  UpdateUserOrganizationProfile: `[${USER}] Update User Organization Profile`,
  DeleteUser: `[${USER}] Delete User`,
  DeleteUserSuccess: `[${USER}] Delete User Success`,
  DeleteUserFailure: `[${USER}] Delete User Failure`,
  CancelMembership: `[${USER}] Cancel Membership`,
  CancelMembershipSuccess: `[${USER}] Cancel Membership Success`,
  CancelMembershipFailure: `[${USER}] Cancel Membership Failure`,
  ReActivateMembership: `[${USER}] Re Activate Membership`,
  ReActivateMembershipSuccess: `[${USER}] Re Activate Membership Success`,
  ReActivateMembershipFailure: `[${USER}] Re Activate Membership Failure`,
  ChangeUser: `[${USER}] Change User`,
  PostConvertToAttorney: `[${USER}] Post Convert To Attorney`,
  PostConvertToAttorneySuccess: `[${USER}] Post Convert To Attorney Success`,
  PostConvertToAttorneyFailure: `[${USER}] Post Convert To Attorney Failure`,
  ShowPersonalDataPopUp: `[${USER}] Show Personal Data PopUp`,
  PreLoginInModal: `[${USER}] Pre Login In Modal`,
};

export class Login implements Action {
  readonly type = UserActionTypes.Login;

  constructor(public payload: LoginModel) {
  }
}

export class LoginSuccess implements Action {
  readonly type = UserActionTypes.LoginSuccess;

  constructor(public payload: User & { access_token: string }) {
  }
}

export class LoginFailure implements Action {
  readonly type = UserActionTypes.LoginFailure;

  constructor(public payload: any) {
  }
}

export class LoginInModal implements Action {
  readonly type = UserActionTypes.LoginInModal;

  constructor(public payload: LoginModel) {
  }
}

export class LoginInModalSuccess implements Action {
  readonly type = UserActionTypes.LoginInModalSuccess;

  constructor(public payload: User & { access_token: string }) {
  }
}

export class LoginInModalFailure implements Action {
  readonly type = UserActionTypes.LoginInModalFailure;

  constructor(public payload: any) {
  }
}

export class Logout implements Action {
  readonly type = UserActionTypes.Logout;
}

export class LogoutSuccess implements Action {
  readonly type = UserActionTypes.LogoutSuccess;

  constructor(public payload?: any) {
  }
}

export class LogoutFailure implements Action {
  readonly type = UserActionTypes.LogoutFailure;

  constructor(public payload?: any) {
  }
}

export class GetUserIdByEVId implements Action {
  readonly type = UserActionTypes.GetUserIdByEVId;

  constructor(public payload: string) {
  }
}

export class GetUserIdByEVIdSuccess implements Action {
  readonly type = UserActionTypes.GetUserIdByEVIdSuccess;

  constructor(public payload: {id: number}) {
  }
}

export class GetUserIdByEVIdFailure implements Action {
  readonly type = UserActionTypes.GetUserIdByEVIdFailure;

  constructor(public payload?: any) {
  }
}

export class GetUser implements Action {
  readonly type = UserActionTypes.GetUser;

  constructor(public payload: string) {
  }
}

export class GetUserSuccess implements Action {
  readonly type = UserActionTypes.GetUserSuccess;

  constructor(public payload: User) {
  }
}

export class GetUserFailure implements Action {
  readonly type = UserActionTypes.GetUserFailure;

  constructor(public payload?: any) {
  }
}

export class GetTokenFailure implements Action {
  readonly type = UserActionTypes.GetTokenFailure;

  constructor() {
  }
}

export class PatchAttorneyFailure implements Action {
  readonly type = UserActionTypes.PatchAttorneyFailure;

  constructor(public payload: User) {
  }
}

export class PatchLoggedInAttorneySuccess implements Action {
  readonly type = UserActionTypes.PatchLoggedInAttorneySuccess;

  constructor(public payload: User) {
  }
}

export class PatchLoggedInAttorneyFailure implements Action {
  readonly type = UserActionTypes.PatchLoggedInAttorneyFailure;

  constructor(public payload: User) {
  }
}

export class PostUserSuccess implements Action {
  readonly type = UserActionTypes.PostUserSuccess;

  constructor(public payload: User & { access_token: string }) {
  }
}

export class SetRegistrationRepresentativeType implements Action {
  readonly type = UserActionTypes.SetRegistrationRepresentativeType;

  constructor(public payload: string) {
  }
}

export class UpdateUserProfile implements Action {
  readonly type = UserActionTypes.UpdateUserProfile;

  constructor(public payload: Profile | any) {
  }
}

export class UpdateUserOrganizationProfile implements Action {
  readonly type = UserActionTypes.UpdateUserOrganizationProfile;

  constructor(public payload: Organization) {
  }
}

export class DeleteUser implements Action {
  readonly type = UserActionTypes.DeleteUser;
}

export class DeleteUserSuccess implements Action {
  readonly type = UserActionTypes.DeleteUserSuccess;

  constructor(public payload?: any) {
  }
}

export class DeleteUserFailure implements Action {
  readonly type = UserActionTypes.DeleteUserFailure;

  constructor(public payload?: any) {
  }
}

export class CancelMembership implements Action {
  readonly type = UserActionTypes.CancelMembership;

  constructor(public payload = false) {
  }
}

export class CancelMembershipSuccess implements Action {
  readonly type = UserActionTypes.CancelMembershipSuccess;

  constructor(public payload: User) {
  }
}

export class CancelMembershipFailure implements Action {
  readonly type = UserActionTypes.CancelMembershipFailure;

  constructor(public payload?: any) {
  }
}

export class ReActivateMembership implements Action {
  readonly type = UserActionTypes.ReActivateMembership;

  constructor(public payload = true) {
  }
}

export class ReActivateMembershipSuccess implements Action {
  readonly type = UserActionTypes.ReActivateMembershipSuccess;

  constructor(public payload: User) {
  }
}

export class ReActivateMembershipFailure implements Action {
  readonly type = UserActionTypes.ReActivateMembershipFailure;

  constructor(public payload?: any) {
  }
}

export class ChangeUser implements Action {
  readonly type = UserActionTypes.ChangeUser;

  constructor(public payload: User) {
  }
}

export class PostConvertToAttorney implements Action {
  readonly type = UserActionTypes.PostConvertToAttorney;

  constructor(public payload: any) {
  }
}

export class PostConvertToAttorneySuccess implements Action {
  readonly type = UserActionTypes.PostConvertToAttorneySuccess;

  constructor(public payload: any) {
  }
}

export class PostConvertToAttorneyFailure implements Action {
  readonly type = UserActionTypes.PostConvertToAttorneyFailure;

  constructor(public payload?: any) {
  }
}

export class ShowPersonalDataPopUp implements Action {
  readonly type = UserActionTypes.ShowPersonalDataPopUp;
}

export class PreLoginInModal implements Action {
  readonly type = UserActionTypes.PreLoginInModal;

  constructor(public payload?: any) {
  }
}

export type UserActionsUnion =
  | GetUser
  | GetUserSuccess
  | GetUserFailure
  | GetUserIdByEVId
  | GetUserIdByEVIdSuccess
  | GetUserIdByEVIdFailure
  | Login
  | LoginSuccess
  | LoginFailure
  | LoginInModal
  | LoginInModalSuccess
  | LoginInModalFailure
  | Logout
  | LogoutSuccess
  | LogoutFailure
  | PatchAttorneyFailure
  | PatchLoggedInAttorneySuccess
  | PatchLoggedInAttorneyFailure
  | PostUserSuccess
  | SetRegistrationRepresentativeType
  | UpdateUserProfile
  | UpdateUserOrganizationProfile
  | DeleteUser
  | DeleteUserSuccess
  | DeleteUserFailure
  | DeleteUserFailure
  | CancelMembership
  | CancelMembershipSuccess
  | CancelMembershipFailure
  | ReActivateMembership
  | ReActivateMembershipSuccess
  | ReActivateMembershipFailure
  | ChangeUser
  | PostConvertToAttorney
  | PostConvertToAttorneySuccess
  | PostConvertToAttorneyFailure
  | ShowPersonalDataPopUp
  | PreLoginInModal;
