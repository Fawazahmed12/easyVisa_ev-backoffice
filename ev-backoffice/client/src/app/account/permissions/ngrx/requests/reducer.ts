import { PermissionsModuleRequestState } from './state';
import { inviteMemberPutRequestReducer } from './invite-member-put/state';
import { permissionsGetRequestReducer } from './permissions-get/state';
import { verifyMemberPostRequestReducer } from './verify-member-post/state';
import { createEmployeePostRequestReducer } from './create-employee-post/state';
import { permissionGetRequestReducer } from './permission-get/state';
import { updateEmployeePutRequestReducer } from './update-employee-put/state';
import { inviteDeleteRequestReducer } from './invite-delete/state';

export function reducer(state: PermissionsModuleRequestState = {}, action): PermissionsModuleRequestState {
  return {
    permissionsGet: permissionsGetRequestReducer(state.permissionsGet, action),
    permissionGet: permissionGetRequestReducer(state.permissionGet, action),
    inviteMemberPut: inviteMemberPutRequestReducer(state.inviteMemberPut, action),
    inviteDeletePermissions: inviteDeleteRequestReducer(state.inviteDeletePermissions, action),
    verifyMemberPost: verifyMemberPostRequestReducer(state.verifyMemberPost, action),
    createEmployeePost: createEmployeePostRequestReducer(state.createEmployeePost, action),
    updateEmployeePut: updateEmployeePutRequestReducer(state.updateEmployeePut, action),
  };
}
