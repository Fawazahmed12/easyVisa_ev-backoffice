import { DashboardModuleRequestEffects } from './requests/effects';
import { ArticlesEffects } from './articles/articles.effects';
import { MarketingEffects } from './marketing/marketing.effects';
import { FinancialEffects } from './financial/financial.effects';

export const effects = [
  ArticlesEffects,
  MarketingEffects,
  FinancialEffects,
  ...DashboardModuleRequestEffects,
];
