import { createFeatureSelector } from '@ngrx/store';

import { MarketingDetails } from '../../models/marketing-details.model';

export const MARKETING = 'Marketing';

export interface MarketingState {
  marketingDetails: MarketingDetails;
}

export const selectMarketingState = createFeatureSelector<MarketingState>(MARKETING);

export const selectMarketingDetails = ({marketingDetails}: MarketingState) => marketingDetails;


