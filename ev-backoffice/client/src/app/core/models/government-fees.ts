import { GovernmentFeeValues } from './government-fee.enum';

export interface GovernmentFeesConst {
  value: GovernmentFeeValues;
  label: string;
  fullLabel: string;
}

export const governmentFeesConst: GovernmentFeesConst[] = [
  {
    value: GovernmentFeeValues.i129f,
    label: 'TEMPLATE.GOVERNMENT_FEES.I129F',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I129F_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.i130,
    label: 'TEMPLATE.GOVERNMENT_FEES.I130',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I130_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.i360,
    label: 'TEMPLATE.GOVERNMENT_FEES.I360',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I360_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.i485,
    label: 'TEMPLATE.GOVERNMENT_FEES.I485',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I485_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.i485_14,
    label: 'TEMPLATE.GOVERNMENT_FEES.I485_14',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I485_14_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.i600_600a,
    label: 'TEMPLATE.GOVERNMENT_FEES.I600_600A',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I600_600A_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.i601,
    label: 'TEMPLATE.GOVERNMENT_FEES.I601',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I601_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.i601a,
    label: 'TEMPLATE.GOVERNMENT_FEES.I601A',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I601A_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.i751,
    label: 'TEMPLATE.GOVERNMENT_FEES.I751',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I751_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.i765,
    label: 'TEMPLATE.GOVERNMENT_FEES.I765',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.I765_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.n400,
    label: 'TEMPLATE.GOVERNMENT_FEES.N400',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.N400_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.n600_n600k,
    label: 'TEMPLATE.GOVERNMENT_FEES.N600_N600K',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.N600_N600K_DESCRIPTION',
  },
  {
    value: GovernmentFeeValues.biometricServiceFee,
    label: '',
    fullLabel: 'TEMPLATE.GOVERNMENT_FEES.BIOMETRIC_SERVICES_FEE',
  },
];
