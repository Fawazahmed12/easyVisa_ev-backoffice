import { Action } from '@ngrx/store';

import { Invite } from '../../models/invite.model';
import { RequestJoin } from '../../models/request-join.model';

import { INVITE_REQUEST } from './invite-request.state';

export const InviteRequestActionTypes = {
  PutInvite: `[${INVITE_REQUEST}] Put Invite`,
  PutInviteSuccess: `[${INVITE_REQUEST}] Put Invite Success`,
  PutInviteFailure: `[${INVITE_REQUEST}] Put Invite Failure`,
  PutRequest: `[${INVITE_REQUEST}] Put Request`,
  PutRequestSuccess: `[${INVITE_REQUEST}] Put Request Success`,
  PutRequestFailure: `[${INVITE_REQUEST}] Put Request Failure`,
  DeleteInvite: `[${INVITE_REQUEST}] Delete Invite`,
  DeleteInviteSuccess: `[${INVITE_REQUEST}] Delete Invite Success`,
  DeleteInviteFailure: `[${INVITE_REQUEST}] Delete Invite Failure`,
  DeleteRequest: `[${INVITE_REQUEST}] Delete Request`,
  DeleteRequestSuccess: `[${INVITE_REQUEST}] Delete Request Success`,
  DeleteRequestFailure: `[${INVITE_REQUEST}] Delete Request Failure`,
  SetInvite: `[${INVITE_REQUEST}] Set Invite`,
  SetRequest: `[${INVITE_REQUEST}] Set Request`,
};

export class PutInvite implements Action {
  readonly type = InviteRequestActionTypes.PutInvite;

  constructor(public payload: any) {
  }
}

export class PutInviteSuccess implements Action {
  readonly type = InviteRequestActionTypes.PutInviteSuccess;

  constructor(public payload: Invite) {
  }
}

export class PutInviteFailure implements Action {
  readonly type = InviteRequestActionTypes.PutInviteFailure;

  constructor(public payload: any) {
  }
}

export class PutRequest implements Action {
  readonly type = InviteRequestActionTypes.PutRequest;

  constructor(public payload: any) {
  }
}

export class PutRequestSuccess implements Action {
  readonly type = InviteRequestActionTypes.PutRequestSuccess;

  constructor(public payload: RequestJoin) {
  }
}

export class PutRequestFailure implements Action {
  readonly type = InviteRequestActionTypes.PutRequestFailure;

  constructor(public payload: any) {
  }
}

export class DeleteRequest implements Action {
  readonly type = InviteRequestActionTypes.DeleteRequest;
}

export class DeleteRequestSuccess implements Action {
  readonly type = InviteRequestActionTypes.DeleteRequestSuccess;

  constructor(public payload: any) {
  }
}

export class DeleteRequestFailure implements Action {
  readonly type = InviteRequestActionTypes.DeleteRequestFailure;

  constructor(public payload: any) {
  }
}

export class DeleteInvite implements Action {
  readonly type = InviteRequestActionTypes.DeleteInvite;

}

export class DeleteInviteSuccess implements Action {
  readonly type = InviteRequestActionTypes.DeleteInviteSuccess;

  constructor(public payload: any) {
  }
}

export class DeleteInviteFailure implements Action {
  readonly type = InviteRequestActionTypes.DeleteInviteFailure;

  constructor(public payload: any) {
  }
}

export class SetInvite implements Action {
  readonly type = InviteRequestActionTypes.SetInvite;

  constructor(public payload: Invite) {
  }
}

export class SetRequest implements Action {
  readonly type = InviteRequestActionTypes.SetRequest;

  constructor(public payload: RequestJoin) {
  }
}

export type InviteRequestActionsUnion =
  | PutInvite
  | PutInviteSuccess
  | PutInviteFailure
  | PutRequest
  | PutRequestSuccess
  | PutRequestFailure
  | DeleteRequest
  | DeleteRequestSuccess
  | DeleteRequestFailure
  | SetInvite
  | SetRequest
  | DeleteInvite
  | DeleteInviteSuccess
  | DeleteInviteFailure;
