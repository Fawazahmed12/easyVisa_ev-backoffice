import { ActionReducerMap } from '@ngrx/store';

import { State } from './state';

import * as fromDashboardSettingsModuleRequest from './requests/reducer';
import * as fromSettings from './settings/settings.reducers';


export const reducers: ActionReducerMap<State> = {
  Settings: fromSettings.reducer,
  DashboardSettingsModuleRequests: fromDashboardSettingsModuleRequest.reducer,
};
