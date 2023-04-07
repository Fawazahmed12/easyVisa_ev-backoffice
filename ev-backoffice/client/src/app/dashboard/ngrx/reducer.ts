import { ActionReducerMap } from '@ngrx/store';

import { State } from './state';

import * as fromArticles from './articles/articles.reducers';
import * as fromMarketing from './marketing/marketing.reducers';
import * as fromFinancial from './financial/financial.reducers';
import * as fromDashboardModuleRequest from './requests/reducer';

export const reducers: ActionReducerMap<State> = {
  Articles: fromArticles.reducer,
  Marketing: fromMarketing.reducer,
  Financial: fromFinancial.reducer,
  DashboardModuleRequests: fromDashboardModuleRequest.reducer,
};
