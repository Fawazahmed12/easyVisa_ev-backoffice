import { articlesGetRequestReducer } from './articles-get/state';
import { articlePostRequestReducer } from './article-post/state';
import { marketingDetailsGetRequestReducer } from './marketing-details-get/state';
import { financialDetailsGetRequestReducer } from './financial-details-get/state';
import { DashboardModuleRequestState } from './state';
import { articleCategoriesGetRequestReducer } from './article-categories-get/state';
import { inviteColleaguesPostRequestReducer } from './invite-colleagues-post/state';

export function reducer(state: DashboardModuleRequestState = {}, action): DashboardModuleRequestState {
  return {
    articlesGet: articlesGetRequestReducer(state.articlesGet, action),
    articlePost: articlePostRequestReducer(state.articlePost, action),
    marketingDetailsGet: marketingDetailsGetRequestReducer(state.marketingDetailsGet, action),
    financialDetailsGet: financialDetailsGetRequestReducer(state.financialDetailsGet, action),
    articleCategoriesGet: articleCategoriesGetRequestReducer(state.articleCategoriesGet, action),
    inviteColleaguesPost: inviteColleaguesPostRequestReducer(state.inviteColleaguesPost, action),
  };
}
