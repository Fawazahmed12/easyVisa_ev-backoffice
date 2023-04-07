import { createFeatureSelector, createSelector } from '@ngrx/store';

import {
  DASHBOARD_MODULE_REQUESTS,
  DashboardModuleRequestState, selectArticleCategoriesGetRequestState,
  selectArticlePostRequestState,
  selectArticlesGetRequestState,
  selectDashboardModuleRequestsState,
  selectFinancialDetailsGetRequestState,
  selectMarketingDetailsGetRequestState,
  selectInviteColleaguesPostRequestState
} from './requests/state';
import {
  ARTICLES,
  ArticlesState,
  selectActiveArticle,
  selectActiveArticleId, selectArticleCategories,
  selectArticles,
  selectArticlesState, selectTotalArticles
} from './articles/articles.state';
import { MARKETING, MarketingState, selectMarketingDetails, selectMarketingState } from './marketing/marketing.state';
import { FINANCIAL, FinancialState, selectFinancialDetails, selectFinancialState } from './financial/financial.state';

export const DASHBOARD_MODULE_STATE = 'DashboardModuleState';

export interface State {
  [DASHBOARD_MODULE_REQUESTS]: DashboardModuleRequestState;
  [ARTICLES]: ArticlesState;
  [MARKETING]: MarketingState;
  [FINANCIAL]: FinancialState;
}

export const selectDashboardModuleState = createFeatureSelector<State>(DASHBOARD_MODULE_STATE);

export const getDashboardModuleRequestsState = createSelector(
  selectDashboardModuleState,
  selectDashboardModuleRequestsState,
);

export const getArticlesGetRequestState = createSelector(
  getDashboardModuleRequestsState,
  selectArticlesGetRequestState,
);

export const getMarketingDetailsGetRequestState = createSelector(
  getDashboardModuleRequestsState,
  selectMarketingDetailsGetRequestState,
);

export const postArticlePostRequestState = createSelector(
  getDashboardModuleRequestsState,
  selectArticlePostRequestState,
);

export const postInviteColleaguesPostRequestState = createSelector(
  getDashboardModuleRequestsState,
  selectInviteColleaguesPostRequestState,
);

export const getArticlesState = createSelector(
  selectDashboardModuleState,
  selectArticlesState,
);

export const getArticles = createSelector(
  getArticlesState,
  selectArticles,
);

export const getMarketingState = createSelector(
  selectDashboardModuleState,
  selectMarketingState,
);

export const getMarketingDetails = createSelector(
  getMarketingState,
  selectMarketingDetails,
);

export const getActiveArticles = createSelector(
  getArticlesState,
  selectActiveArticle,
);

export const getActiveArticlesId = createSelector(
  getArticlesState,
  selectActiveArticleId,
);

export const getArticleCategories = createSelector(
  getArticlesState,
  selectArticleCategories,
);

export const getTotalArticles = createSelector(
  getArticlesState,
  selectTotalArticles,
);

export const getFinancialState = createSelector(
  selectDashboardModuleState,
  selectFinancialState,
);

export const getFinancialDetails = createSelector(
  getFinancialState,
  selectFinancialDetails,
);

export const getFinancialDetailsGetRequestState = createSelector(
  getDashboardModuleRequestsState,
  selectFinancialDetailsGetRequestState,
);

export const getArticleCategoriesGetRequestState = createSelector(
  getDashboardModuleRequestsState,
  selectArticleCategoriesGetRequestState,
);
