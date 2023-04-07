import { ActionReducerMap } from '@ngrx/store';

import { State } from './state';

import * as fromWarnings from './warnings/warnings.reducers';
import * as fromDispositions from './dispositions/dispositions.reducers';
import * as fromTaskQueueModuleRequest from './requests/reducer';

export const reducers: ActionReducerMap<State> = {
  Warnings: fromWarnings.reducer,
  Dispositions: fromDispositions.reducer,
  TaskQueueModuleRequests: fromTaskQueueModuleRequest.reducer,
};
