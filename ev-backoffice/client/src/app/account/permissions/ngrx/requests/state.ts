import { RequestState } from '../../../../core/ngrx/utils';

import { OrganizationEmployee } from '../../models/organization-employee.model';


export const PERMISSIONS_MODULE_REQUESTS = 'PermissionsModuleRequests';

export interface PermissionsModuleRequestState {
  inviteMemberPut?: RequestState<any>;
  inviteDeletePermissions?: RequestState<any>;
  verifyMemberPost?: RequestState<any>;
  createEmployeePost?: RequestState<any>;
  updateEmployeePut?: RequestState<any>;
  permissionsGet?: RequestState<OrganizationEmployee[]>;
  permissionGet?: RequestState<OrganizationEmployee>;
}

export const selectPermissionsModuleRequestsState = (state) => state[PERMISSIONS_MODULE_REQUESTS];

export const selectInviteMemberPutRequestState = (state: PermissionsModuleRequestState) => state.inviteMemberPut;
export const selectInviteDeleteRequestState = (state: PermissionsModuleRequestState) => state.inviteDeletePermissions;
export const selectVerifyMemberPostRequestState = (state: PermissionsModuleRequestState) => state.verifyMemberPost;
export const selectCreateEmployeePostRequestState = (state: PermissionsModuleRequestState) => state.createEmployeePost;
export const selectUpdateEmployeePutRequestState = (state: PermissionsModuleRequestState) => state.updateEmployeePut;
export const selectPermissionsGetRequestState = (state: PermissionsModuleRequestState) => state.permissionsGet;
export const selectPermissionGetRequestState = (state: PermissionsModuleRequestState) => state.permissionGet;

export { inviteMemberPutRequestHandler } from './invite-member-put/state';
export { inviteDeleteRequestHandler } from './invite-delete/state';
export { verifyMemberPostRequestHandler } from './verify-member-post/state';
export { createEmployeePostRequestHandler } from './create-employee-post/state';
export { updateEmployeePutRequestHandler } from './update-employee-put/state';
export { permissionsGetRequestHandler } from './permissions-get/state';
export { permissionGetRequestHandler } from './permission-get/state';
