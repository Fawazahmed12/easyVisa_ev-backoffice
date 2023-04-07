import { PracticeArea } from './practice.enum';

export interface Practices {
  label: string;
  value: PracticeArea;
  checked?: boolean;
}

export const practice: Practices[] = [
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.BANKRUPTCY_AND_DEBT',
    value: PracticeArea.BANKRUPTCY_AND_DEBT,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.BUSINESS',
    value: PracticeArea.BUSINESS,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.CIVIL_RIGHTS',
    value: PracticeArea.CIVIL_RIGHTS,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.CONSUMER_PROTECTION',
    value: PracticeArea.CONSUMER_PROTECTION,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.CRIMINAL_DEFENSE',
    value: PracticeArea.CRIMINAL_DEFENSE,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.EMPLOYMENT_AND_LABOR',
    value: PracticeArea.EMPLOYMENT_AND_LABOR,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.ESTATE_PLANNING',
    value: PracticeArea.ESTATE_PLANNING,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.FAMILY',
    value: PracticeArea.FAMILY,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.GOVERNMENT',
    value: PracticeArea.GOVERNMENT,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.INTELLECTUAL_PROPERTY',
    value: PracticeArea.INTELLECTUAL_PROPERTY,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.IMMIGRATION',
    value: PracticeArea.IMMIGRATION,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.LAWSUITS_AND_DISPUTES',
    value: PracticeArea.LAWSUITS_AND_DISPUTES,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.PERSONAL_INJURY',
    value: PracticeArea.PERSONAL_INJURY,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.PRACTICE.REAL_ESTATE',
    value: PracticeArea.REAL_ESTATE,
  },
];
