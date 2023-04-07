import { ActionReducerMap } from '@ngrx/store';

import { State } from './state';

import * as fromPermissionsModuleRequest from './requests/reducer';
import * as fromPermissions from './permissions/permissions.reducers';

export const reducers: ActionReducerMap<State> = {
  PermissionsModuleRequests: fromPermissionsModuleRequest.reducer,
  Permissions: fromPermissions.reducer,
};
