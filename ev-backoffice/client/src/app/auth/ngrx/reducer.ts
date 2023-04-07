import { ActionReducerMap } from '@ngrx/store';

import { State } from './state';

import * as fromAttorneySignUpInfo from './attorney-sign-up-info/attorney-sign-up-info.reducers';
import * as fromAuthModuleRequest from './requests/reducer';

export const reducers: ActionReducerMap<State> = {
  AttorneySignUpInfo: fromAttorneySignUpInfo.reducer,
  AuthModuleRequests: fromAuthModuleRequest.reducer,
};
