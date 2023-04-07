import { BenefitCategories } from './benefit-categories.enum';

export interface FeeSchedule {
  amount: number;
  benefitCategory: BenefitCategories;
  id: number;
  representativeId: number;
}
