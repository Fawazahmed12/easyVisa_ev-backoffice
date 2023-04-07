import { DegreesValues } from './degree.enum';

export interface Degrees {
  fullLabel: string;
  label: string;
  value: DegreesValues;
}

export const degree: Degrees[] = [
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.ASSOCIATE_OF_ARTS',
    label: 'A.A.',
    value: DegreesValues.ASSOCIATE_OF_ARTS,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.ASSOCIATE_OF_SCIENCE',
    label: 'A.S.',
    value: DegreesValues.ASSOCIATE_OF_SCIENCE,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.ASSOCIATE_OF_APPLIED_SCIENCE',
    label: 'A.A.S.',
    value: DegreesValues.ASSOCIATE_OF_APPLIED_SCIENCE,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.BACHELOR_OF_ARTS',
    label: 'B.A.',
    value: DegreesValues.BACHELOR_OF_ARTS,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.BACHELOR_OF_SCIENCE',
    label: 'B.S.',
    value: DegreesValues.BACHELOR_OF_SCIENCE,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.BACHELOR_OF_FINE_ARTS',
    label: 'B.F.A.',
    value: DegreesValues.BACHELOR_OF_FINE_ARTS,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.BACHELOR_OF_APPLIED_SCIENCE',
    label: 'B.A.S.',
    value: DegreesValues.BACHELOR_OF_APPLIED_SCIENCE,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.MASTER_OF_ARTS',
    label: 'M.A.',
    value: DegreesValues.MASTER_OF_ARTS,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.MASTER_OF_SCIENCE',
    label: 'M.S.',
    value: DegreesValues.MASTER_OF_SCIENCE,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.MASTER_OF_BUSINESS_ADMINISTRATION',
    label: 'M.B.A.',
    value: DegreesValues.MASTER_OF_BUSINESS_ADMINISTRATION,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.MASTER_OF_FINE_ARTS',
    label: 'M.F.A.',
    value: DegreesValues.MASTER_OF_FINE_ARTS,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.DOCTOR_OF_PHILOSOPHY',
    label: 'Ph.D.',
    value: DegreesValues.DOCTOR_OF_PHILOSOPHY,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.JURIS_DOCTOR',
    label: 'J.D.',
    value: DegreesValues.JURIS_DOCTOR,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.DOCTOR_OF_MEDICINE',
    label: 'M.D.',
    value: DegreesValues.DOCTOR_OF_MEDICINE,
  },
  {
    fullLabel: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.DEGREE.DOCTOR_OF_DENTAL_SURGERY',
    label: 'D.D.S.',
    value: DegreesValues.DOCTOR_OF_DENTAL_SURGERY,
  },
];
