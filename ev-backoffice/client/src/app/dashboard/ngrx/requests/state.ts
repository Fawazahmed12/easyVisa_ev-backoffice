import { RequestState } from '../../../core/ngrx/utils';
import { Article } from '../../articles/models/article.model';
import { MarketingDetails } from '../../models/marketing-details.model';
import { FinancialDetails } from '../../models/financial-details.model';
import { ArticleCategory } from '../../articles/models/article-category.model';
import { Email } from '../../../core/models/email.model';

export const DASHBOARD_MODULE_REQUESTS = 'DashboardModuleRequests';

export interface DashboardModuleRequestState {
  articlesGet?: RequestState<Article[]>;
  articlePost?: RequestState<Article>;
  marketingDetailsGet?: RequestState<MarketingDetails>;
  financialDetailsGet?: RequestState<FinancialDetails>;
  articleCategoriesGet?: RequestState<ArticleCategory[]>;
  inviteColleaguesPost?: RequestState<Email>;
}

export const selectDashboardModuleRequestsState = (state) => state[DASHBOARD_MODULE_REQUESTS];

export const selectArticlesGetRequestState = (state: DashboardModuleRequestState) => state.articlesGet;

export const selectArticlePostRequestState = (state: DashboardModuleRequestState) => state.articlePost;

export const selectMarketingDetailsGetRequestState = (state: DashboardModuleRequestState) => state.marketingDetailsGet;

export const selectFinancialDetailsGetRequestState = (state: DashboardModuleRequestState) => state.financialDetailsGet;

export const selectArticleCategoriesGetRequestState = (state: DashboardModuleRequestState) => state.articleCategoriesGet;

export const selectInviteColleaguesPostRequestState = (state: DashboardModuleRequestState) => state.inviteColleaguesPost;

export { articlesGetRequestHandler } from './articles-get/state';
export { marketingDetailsGetRequestHandler } from './marketing-details-get/state';
export { financialDetailsGetRequestHandler } from './financial-details-get/state';
export { inviteColleaguesPostRequestHandler } from './invite-colleagues-post/state';
