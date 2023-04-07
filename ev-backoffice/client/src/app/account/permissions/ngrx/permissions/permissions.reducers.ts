import { adapter, PermissionsState } from './permissions.state';
import {
  DeleteInviteSuccess,
  GetPermissionsSuccess,
  GetPermissionSuccess,
  PermissionsActionsUnion,
  PermissionsActionTypes, PutUpdateEmployeeSuccess,
  UpdatePermissions
} from './permissions.actions';


export const initialState: PermissionsState = adapter.getInitialState({
  activePermissionId: null,
});

export function reducer(state = initialState, action: PermissionsActionsUnion) {
  switch (action.type) {

    case PermissionsActionTypes.GetPermission:
    case PermissionsActionTypes.GetPermissions: {
      return {
        ...state,
        activePermissionId: null
      };
    }
    case PermissionsActionTypes.GetPermissionsSuccess: {
      return {
        ...adapter.setAll((action as GetPermissionsSuccess).payload, state),
      };
    }

    case PermissionsActionTypes.DeleteInviteSuccess: {
      return {
        ...adapter.removeOne((action as DeleteInviteSuccess).payload, state),
      };
    }

    case PermissionsActionTypes.GetPermissionSuccess: {
      return {
        ...adapter.upsertOne((action as GetPermissionSuccess).payload, state),
        activePermissionId: (action as GetPermissionSuccess).payload.employeeId
      };
    }

    case PermissionsActionTypes.UpdatePermissions: {
      return {
        ...adapter.updateMany((action as UpdatePermissions).payload, state),
      };
    }

    case PermissionsActionTypes.PutUpdateEmployeeSuccess: {
      const payload = (action as PutUpdateEmployeeSuccess).payload;
      return {
        ...adapter.updateOne({id: payload.id, changes: payload}, state),
      };
    }

    default: {
      return state;
    }
  }
}
