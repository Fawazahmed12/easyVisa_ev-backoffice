import { Action } from '@ngrx/store';

import { Attorney } from '../../../core/models/attorney.model';
import { ReferringUserModel } from '../../models/referring-user.model';

import { ATTORNEY_SIGN_UP_INFO } from './attorney-sign-up-info.state';

export const AttorneySignUpInfoActionTypes  = {
  GetReferringUser: `[${ATTORNEY_SIGN_UP_INFO}] Get Referring User`,
  GetReferringUserSuccess: `[${ATTORNEY_SIGN_UP_INFO}] Get Referring User Success`,
  GetReferringUserFailure: `[${ATTORNEY_SIGN_UP_INFO}] Get Referring User Failure`,
  PostAttorneyUser: `[${ATTORNEY_SIGN_UP_INFO}] Post Attorney User`,
  PostAttorneyUserSuccess: `[${ATTORNEY_SIGN_UP_INFO}] Post Attorney User Success`,
  PostAttorneyUserFailure: `[${ATTORNEY_SIGN_UP_INFO}] Post Attorney User Failure`,
  PostAddReferral: `[${ATTORNEY_SIGN_UP_INFO}] Post Add Referral`,
  PostAddReferralSuccess: `[${ATTORNEY_SIGN_UP_INFO}] Post Add Referral Success`,
  PostAddReferralFailure: `[${ATTORNEY_SIGN_UP_INFO}] Post Add Referral Failure`,
};

export class GetReferringUser implements Action {
  readonly type = AttorneySignUpInfoActionTypes.GetReferringUser;

  constructor(public payload: string) {}
}

export class GetReferringUserSuccess implements Action {
  readonly type = AttorneySignUpInfoActionTypes.GetReferringUserSuccess;

  constructor(public payload: ReferringUserModel) {}
}

export class GetReferringUserFailure implements Action {
  readonly type = AttorneySignUpInfoActionTypes.GetReferringUserFailure;

  constructor(public payload: any) {}
}

export class PostAttorneyUser implements Action {
  readonly type = AttorneySignUpInfoActionTypes.PostAttorneyUser;

  constructor(public payload: Attorney) {}
}

export class PostAttorneyUserSuccess implements Action {
  readonly type = AttorneySignUpInfoActionTypes.PostAttorneyUserSuccess;

  constructor(public payload: string) {}
}

export class PostAttorneyUserFailure implements Action {
  readonly type = AttorneySignUpInfoActionTypes.PostAttorneyUserFailure;

  constructor(public payload: any) {}
}

export class PostAddReferral implements Action {
  readonly type = AttorneySignUpInfoActionTypes.PostAddReferral;

  constructor(public payload: {email: string}) {}
}

export class PostAddReferralSuccess implements Action {
  readonly type = AttorneySignUpInfoActionTypes.PostAddReferralSuccess;

  constructor(public payload: string) {}
}

export class PostAddReferralFailure implements Action {
  readonly type = AttorneySignUpInfoActionTypes.PostAddReferralFailure;

  constructor(public payload: any) {}
}

export type AttorneySignUpInfoActionsUnion =
  | GetReferringUser
  | GetReferringUserSuccess
  | GetReferringUserFailure
  | PostAttorneyUser
  | PostAttorneyUserSuccess
  | PostAttorneyUserFailure
  | PostAddReferral
  | PostAddReferralSuccess
  | PostAddReferralFailure;
