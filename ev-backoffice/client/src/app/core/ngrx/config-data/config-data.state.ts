import { FeeDetails } from '../../models/fee-details.model';
import { createFeatureSelector, createSelector } from '@ngrx/store';
import { GovernmentFee } from '../../models/government-fee.model';
import { Benefits } from '../../models/benefits.model';

export const CONFIG_DATA = 'ConfigData';

export interface ConfigDataState {
  feeDetails: FeeDetails;
  governmentFee: GovernmentFee;
  benefits: Benefits;
}

export const selectConfigDataState = createFeatureSelector<ConfigDataState>(CONFIG_DATA);

export const selectFeeDetails = ({feeDetails}: ConfigDataState) => feeDetails;
export const selectGovernmentFee = ({governmentFee}: ConfigDataState) => governmentFee;
export const selectBenefits = ({benefits}: ConfigDataState) => benefits;

export const getFeeDetails = createSelector(
  selectConfigDataState,
  selectFeeDetails,
);

export const getGovernmentFee = createSelector(
  selectConfigDataState,
  selectGovernmentFee,
);

export const getBenefits = createSelector(
  selectConfigDataState,
  selectBenefits,
);

export const getBenefitsCategories = createSelector(
  getBenefits,
  (benefits) => benefits && benefits.benefitCategories || null
);

export const getNoPetitionerBenefitGroups = createSelector(
  getBenefits,
  (benefits) => benefits && benefits.noPetitionerBenefitGroups || null
);

export const getBenefitGroupsNoDerivatives = createSelector(
  getBenefits,
  (benefits) => benefits && benefits.benefitGroupsNoDerivatives || null
);

export const getBenefitGroupsWithDerivatives = createSelector(
  getBenefits,
  (benefits) => benefits && benefits.benefitGroupsWithDerivatives || null
);

export const getSearchGroups = createSelector(
  getBenefits,
  (benefits) => benefits && benefits.searchGroups || null
);

export const getAllBenefitGroups = createSelector(
  getBenefitGroupsNoDerivatives,
  getBenefitGroupsWithDerivatives,
  getNoPetitionerBenefitGroups,
  (benefitGroupsNoDerivatives, benefitGroupsWithDerivatives, noPetitionerBenefitGroups) => [
    ...benefitGroupsNoDerivatives,
    ...benefitGroupsWithDerivatives,
    ...noPetitionerBenefitGroups
  ]
);

export const getNoPetitionerBenefitCategories = createSelector(
  getBenefitsCategories,
  getNoPetitionerBenefitGroups,
  (benefitsCategories, noPetitionerBenefitGroups) => benefitsCategories && benefitsCategories.filter(
    benefitCategory => noPetitionerBenefitGroups.some(noPetitionerBenefitGroup =>
      benefitCategory.benefitGroup === noPetitionerBenefitGroup.value)
  ) || null
);

export const getBenefitCategoriesNoDerivatives = createSelector(
  getBenefitsCategories,
  getBenefitGroupsNoDerivatives,
  (benefitsCategories, benefitGroups) => benefitsCategories && benefitsCategories.filter(
    benefitCategory => benefitGroups.some(benefitGroup =>
      benefitCategory.benefitGroup === benefitGroup.value)
  ) || null
);

export const getBenefitCategoriesWithDerivatives = createSelector(
  getBenefitsCategories,
  getBenefitGroupsWithDerivatives,
  (benefitsCategories, benefitGroups) => benefitsCategories && benefitsCategories.filter(
    benefitCategory => benefitGroups.some(benefitGroup =>
      benefitCategory.benefitGroup === benefitGroup.value)
  ) || null
);
