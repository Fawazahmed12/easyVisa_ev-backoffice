import { ActionReducerMap } from '@ngrx/store';

import { State } from './state';

import * as fromProgressStatus from './progress-statuses/progress-statuses.reducers';
import * as fromProgressStatusModuleRequest from './requests/reducer';

export const reducers: ActionReducerMap<State> = {
  ProgressStatuses: fromProgressStatus.reducer,
  ProgressStatusesModuleRequests: fromProgressStatusModuleRequest.reducer,
};
