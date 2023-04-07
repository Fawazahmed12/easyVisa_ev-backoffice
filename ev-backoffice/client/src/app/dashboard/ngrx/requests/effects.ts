import { ArticlesGetRequestEffects } from './articles-get/state';
import { ArticlePostRequestEffects } from './article-post/state';
import { MarketingDetailsGetRequestEffects } from './marketing-details-get/state';
import { FinancialDetailsGetRequestEffects } from './financial-details-get/state';
import { ArticleCategoriesGetRequestEffects } from './article-categories-get/state';
import { InviteColleaguesPostRequestEffects } from './invite-colleagues-post/state';


export const DashboardModuleRequestEffects = [
  ArticlesGetRequestEffects,
  ArticlePostRequestEffects,
  MarketingDetailsGetRequestEffects,
  FinancialDetailsGetRequestEffects,
  ArticleCategoriesGetRequestEffects,
  InviteColleaguesPostRequestEffects
];
