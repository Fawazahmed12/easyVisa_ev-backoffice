import { createFeatureSelector } from '@ngrx/store';

import { FinancialDetails } from '../../models/financial-details.model';

export const FINANCIAL = 'Financial';

export interface FinancialState {
  financialDetails: FinancialDetails;
}

export const selectFinancialState = createFeatureSelector<FinancialState>(FINANCIAL);

export const selectFinancialDetails = ({financialDetails}: FinancialState) => financialDetails;


