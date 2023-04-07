import { BenefitCategories } from './benefit-categories.enum';
import { BenefitGroups } from './benefit-groups.enum';

export interface BenefitCategoryConst {
  value: BenefitCategories;
  label: string;
  fullLabel: string;
  group: BenefitGroups;
  disabled?: boolean;
  price?: number;
  checked?: boolean;
  note?: string;
  searchLabel?: string;
}

export const benefitCategories: BenefitCategoryConst[] = [
  {
    value: BenefitCategories.IR1,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.IR_1',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.IR_1_DESCRIPTION',
    group: BenefitGroups.IMMEDIATE_RELATIVE_VISA
  },
  {
    value: BenefitCategories.IR2,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.IR_2',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.IR_2_DESCRIPTION',
    group: BenefitGroups.IMMEDIATE_RELATIVE_VISA
  },
  {
    value: BenefitCategories.IR5,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.IR_5',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.IR_5_DESCRIPTION',
    group: BenefitGroups.IMMEDIATE_RELATIVE_VISA
  },
  {
    value: BenefitCategories.F1_A,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F1',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F1_DESCRIPTION',
    group: BenefitGroups.FAMILY_PREFERENCE_VISA
  },
  {
    value: BenefitCategories.F2_A,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F2',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F2_DESCRIPTION',
    group: BenefitGroups.FAMILY_PREFERENCE_VISA
  },
  {
    value: BenefitCategories.F3_A,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F3',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F3_DESCRIPTION',
    group: BenefitGroups.FAMILY_PREFERENCE_VISA
  },
  {
    value: BenefitCategories.F4_A,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F4',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F4_DESCRIPTION',
    group: BenefitGroups.FAMILY_PREFERENCE_VISA
  },
  {
    value: BenefitCategories.F1_B,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F1',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F1_DESCRIPTION',
    group: BenefitGroups.FAMILY_PREFERENCE_VISA
  },
  {
    value: BenefitCategories.F2_B,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F2',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F2_DESCRIPTION',
    group: BenefitGroups.FAMILY_PREFERENCE_VISA
  },
  {
    value: BenefitCategories.F3_B,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F3',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F3_DESCRIPTION',
    group: BenefitGroups.FAMILY_PREFERENCE_VISA
  },
  {
    value: BenefitCategories.F4_B,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F4',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.F4_DESCRIPTION',
    group: BenefitGroups.FAMILY_PREFERENCE_VISA
  },
  {
    value: BenefitCategories.K1K3,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.K_1_K_3',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.K_1_K_3_DESCRIPTION',
    group: BenefitGroups.VISA_FIANCE_SPOUSE
  },
  {
    value: BenefitCategories.K2K4,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.K_2_K_4',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.K_2_K_4_DESCRIPTION',
    group: BenefitGroups.VISA_FIANCE_SPOUSE
  },
  {
    value: BenefitCategories.NATURALIZATION,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.NATURALIZATION',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.NATURALIZATION_DESCRIPTION',
    group: BenefitGroups.NATURALIZATION
  },
  {
    value: BenefitCategories.LPRSPOUSE,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.LPR_SPOUSE',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.LPR_DESCRIPTION_1',
    group: BenefitGroups.PERMANENT_RESIDENCE
  },
  {
    value: BenefitCategories.LPRCHILD,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.LPR_CHILD',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.LPR_DESCRIPTION_2',
    group: BenefitGroups.PERMANENT_RESIDENCE
  },
  {
    value: BenefitCategories.REMOVECOND,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.REMOVE_CONDITIONS',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.REMOVE_CONDITIONS_DESCRIPTION',
    group: BenefitGroups.REMOVE_CONDITIONS
  },
  {
    value: BenefitCategories.SIX01,
    label: '601',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.601_DESCRIPTION',
    group: BenefitGroups.MISCELLANEOUS
  },
  {
    value: BenefitCategories.SIX01A,
    label: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.601A',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.601A_DESCRIPTION',
    group: BenefitGroups.MISCELLANEOUS
  },
  {
    value: BenefitCategories.EAD,
    label: '765',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.765_DESCRIPTION',
    group: BenefitGroups.MISCELLANEOUS
  },
  {
    value: BenefitCategories.DISABILITY,
    label: '648',
    fullLabel: 'TEMPLATE.IMMIGRATION_BENEFIT_CATEGORIES.648_DESCRIPTION',
    group: BenefitGroups.MISCELLANEOUS
  },
];
