import { Action } from '@ngrx/store';

import { OrganizationEmployee } from '../../models/organization-employee.model';

import { PERMISSIONS } from './permissions.state';


export const PermissionsActionTypes = {
  GetPermissions: `[${PERMISSIONS}] Get Permissions`,
  GetPermissionsSuccess: `[${PERMISSIONS}] Get Permissions Success`,
  GetPermissionsFailure: `[${PERMISSIONS}] Get Permissions Failure`,
  GetPermission: `[${PERMISSIONS}] Get Permission`,
  GetPermissionSuccess: `[${PERMISSIONS}] Get Permission Success`,
  GetPermissionFailure: `[${PERMISSIONS}] Get Permission Failure`,
  UpdatePermissions: `[${PERMISSIONS}] Update Permissions`,
  PutInviteMember: `[${PERMISSIONS}] Put Invite Member`,
  PutInviteMemberSuccess: `[${PERMISSIONS}] Put Invite Member Success`,
  PutInviteMemberFailure: `[${PERMISSIONS}] Put Invite Member Failure`,
  PutUpdateEmployee: `[${PERMISSIONS}] Put Update Employee`,
  PutUpdateEmployeeSuccess: `[${PERMISSIONS}] Put Update Employee Success`,
  PutUpdateEmployeeFailure: `[${PERMISSIONS}] Put Update Employee Failure`,
  OpenWithdrawInviteModal: `[${PERMISSIONS}] Open Withdraw Invite Modal`,
  DeleteInvite: `[${PERMISSIONS}] Delete Invite`,
  DeleteInviteSuccess: `[${PERMISSIONS}] Delete Invite Success`,
  DeleteInviteFailure: `[${PERMISSIONS}] Delete Invite Failure`,
};

export class GetPermissions implements Action {
  readonly type = PermissionsActionTypes.GetPermissions;

  constructor(public payload: any) {
  }
}

export class GetPermissionsSuccess implements Action {
  readonly type = PermissionsActionTypes.GetPermissionsSuccess;

  constructor(public payload: OrganizationEmployee[]) {
  }
}

export class GetPermissionsFailure implements Action {
  readonly type = PermissionsActionTypes.GetPermissionsFailure;

  constructor(public payload?: any) {
  }
}

export class GetPermission implements Action {
  readonly type = PermissionsActionTypes.GetPermission;

  constructor(public payload?: any) {
  }
}

export class GetPermissionSuccess implements Action {
  readonly type = PermissionsActionTypes.GetPermissionSuccess;

  constructor(public payload: OrganizationEmployee) {
  }
}

export class GetPermissionFailure implements Action {
  readonly type = PermissionsActionTypes.GetPermissionFailure;

  constructor(public payload?: any) {
  }
}

export class UpdatePermissions implements Action {
  readonly type = PermissionsActionTypes.UpdatePermissions;

  constructor(public payload: any) {
  }
}

export class PutUpdateEmployee implements Action {
  readonly type = PermissionsActionTypes.PutUpdateEmployee;

  constructor(public payload: OrganizationEmployee) {
  }
}

export class PutUpdateEmployeeSuccess implements Action {
  readonly type = PermissionsActionTypes.PutUpdateEmployeeSuccess;

  constructor(public payload: any) {
  }
}

export class PutUpdateEmployeeFailure implements Action {
  readonly type = PermissionsActionTypes.PutUpdateEmployeeFailure;

  constructor(public payload?: any) {
  }
}

export class PutInviteMember implements Action {
  readonly type = PermissionsActionTypes.PutInviteMember;
}

export class PutInviteMemberSuccess implements Action {
  readonly type = PermissionsActionTypes.PutInviteMemberSuccess;

  constructor(public payload: any) {
  }
}

export class PutInviteMemberFailure implements Action {
  readonly type = PermissionsActionTypes.PutInviteMemberFailure;

  constructor(public payload?: any) {
  }
}

export class OpenWithdrawInviteModal implements Action {
  readonly type = PermissionsActionTypes.OpenWithdrawInviteModal;

  constructor(public payload?: any) {
  }
}

export class DeleteInvite implements Action {
  readonly type = PermissionsActionTypes.DeleteInvite;

  constructor(public payload: any) {
  }
}

export class DeleteInviteSuccess implements Action {
  readonly type = PermissionsActionTypes.DeleteInviteSuccess;

  constructor(public payload: any) {
  }
}

export class DeleteInviteFailure implements Action {
  readonly type = PermissionsActionTypes.DeleteInviteFailure;

  constructor(public payload: any) {
  }
}


export type PermissionsActionsUnion =
  | GetPermissions
  | GetPermissionsSuccess
  | GetPermissionsFailure
  | GetPermission
  | GetPermissionSuccess
  | GetPermissionFailure
  | UpdatePermissions
  | PutInviteMember
  | PutInviteMemberSuccess
  | PutInviteMemberFailure
  | PutUpdateEmployee
  | PutUpdateEmployeeSuccess
  | PutUpdateEmployeeFailure
  | OpenWithdrawInviteModal
  | DeleteInvite
  | DeleteInviteSuccess
  | DeleteInviteFailure;
