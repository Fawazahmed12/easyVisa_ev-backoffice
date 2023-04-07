import { createFeatureSelector, createSelector } from '@ngrx/store';

import {
  PERMISSIONS_MODULE_REQUESTS,
  PermissionsModuleRequestState,
  selectPermissionsModuleRequestsState,
  selectInviteMemberPutRequestState,
  selectPermissionsGetRequestState,
  selectVerifyMemberPostRequestState,
  selectCreateEmployeePostRequestState,
  selectUpdateEmployeePutRequestState,
  selectPermissionGetRequestState,
  selectInviteDeleteRequestState,
} from './requests/state';
import {
  PERMISSIONS,
  PermissionsState, selectActivePermission, selectActivePermissionId,
  selectPermissions,
  selectPermissionsEntities,
  selectPermissionsState
} from './permissions/permissions.state';


export const PERMISSIONS_MODULE_STATE = 'PermissionsModuleState';

export interface State {
  [PERMISSIONS]: PermissionsState;
  [PERMISSIONS_MODULE_REQUESTS]: PermissionsModuleRequestState;
}

export const selectPermissionsModuleState = createFeatureSelector<State>(PERMISSIONS_MODULE_STATE);

export const getPermissionsModuleRequestsState = createSelector(
  selectPermissionsModuleState,
  selectPermissionsModuleRequestsState,
);

export const getPermissionsGetRequestState = createSelector(
  getPermissionsModuleRequestsState,
  selectPermissionsGetRequestState,
);

export const getPermissionGetRequestState = createSelector(
  getPermissionsModuleRequestsState,
  selectPermissionGetRequestState,
);

export const getPermissionsState = createSelector(
  selectPermissionsModuleState,
  selectPermissionsState,
);

export const getPermissions = createSelector(
  getPermissionsState,
  selectPermissions,
);

export const getPermissionsEntities = createSelector(
  getPermissionsState,
  selectPermissionsEntities,
);

export const getInviteMemberPutRequestState = createSelector(
  getPermissionsModuleRequestsState,
  selectInviteMemberPutRequestState,
);

export const getVerifyMemberPostRequestState = createSelector(
  getPermissionsModuleRequestsState,
  selectVerifyMemberPostRequestState,
);

export const getCreateEmployeePostRequestState = createSelector(
  getPermissionsModuleRequestsState,
  selectCreateEmployeePostRequestState,
);

export const getUpdateEmployeePutRequestState = createSelector(
  getPermissionsModuleRequestsState,
  selectUpdateEmployeePutRequestState,
);

export const getInviteDeleteRequestState = createSelector(
  getPermissionsModuleRequestsState,
  selectInviteDeleteRequestState,
);

export const getActivePermissionId = createSelector(
  getPermissionsState,
  selectActivePermissionId,
);

export const getActivePermission = createSelector(
  getPermissionsState,
  selectActivePermission,
);
