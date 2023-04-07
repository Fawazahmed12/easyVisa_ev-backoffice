import { HonorValues } from './honor.enum';

export interface Honors {
  label: string;
  value: HonorValues;
}

export const honor: Honors[] = [
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.HONORS.SUMMA_CUM_LAUDE',
    value: HonorValues.SUMMA_CUM_LAUDE,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.HONORS.MAGNA_CUM_LAUDE',
    value: HonorValues.MAGNA_CUM_LAUDE,
  },
  {
    label: 'TEMPLATE.ACCOUNT.PROFILE.EDUCATION_FORM.HONORS.CUM_LAUDE',
    value: HonorValues.CUM_LAUDE,
  },
];
