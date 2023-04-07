import { Action } from '@ngrx/store';

import { AUTH } from './auth.state';

export const AuthActionTypes  = {
  SetRedirectUrl: `[${AUTH}] Set Redirect Url`,
  ResetRedirectUrl: `[${AUTH}] Reset Redirect Url`,
  PutPassword: `[${AUTH}] Put Password`,
  PutPasswordSuccess: `[${AUTH}] Put Password Success`,
  PutPasswordFailure: `[${AUTH}] Put Password Failure`,
};

export class SetRedirectUrl implements Action {
  readonly type = AuthActionTypes.SetRedirectUrl;

  constructor(public payload: string) {}
}

export class ResetRedirectUrl implements Action {
  readonly type = AuthActionTypes.ResetRedirectUrl;
}

export class PutPassword implements Action {
  readonly type = AuthActionTypes.PutPassword;

  constructor(public payload: any) {}
}

export class PutPasswordSuccess implements Action {
  readonly type = AuthActionTypes.PutPasswordSuccess;

  constructor(public payload: {access_token: string}) {}
}

export class PutPasswordFailure implements Action {
  readonly type = AuthActionTypes.PutPasswordFailure;

  constructor(public payload: any) {}
}


export type AuthActionUnion =
  | SetRedirectUrl
  | ResetRedirectUrl
  | ResetRedirectUrl
  | PutPassword
  | PutPasswordSuccess
  | PutPasswordFailure;
