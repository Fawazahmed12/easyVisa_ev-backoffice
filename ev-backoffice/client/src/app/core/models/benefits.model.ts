export interface Benefits {
  benefitGroupsNoDerivatives: BenefitGroupModel[];
  benefitGroupsWithDerivatives: BenefitGroupModel[];
  noPetitionerBenefitGroups: BenefitGroupModel[];
  benefitCategories: BenefitCategoryModel[];
  searchGroups: BenefitCategoryModel[];
  disabledLPRCategories: string[];
  disabledLPRDerivativeCategories: string[];
  disabledUSCitizenCategories: string[];
  disabledUSDerivativeCategories: string[];
}

export interface BenefitGroupModel {
  value: string;
  label: string;
  note: string;
  shortName?: string;
}

export interface BenefitCategoryModel {
  value: string;
  label: string;
  fullLabel: string;
  benefitGroup: string;
  disabled: boolean;
  note: string;
  checked?: boolean;
  searchLabel?: string;
}
