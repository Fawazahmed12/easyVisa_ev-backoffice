import { SourceAlertValues } from './source-alert.enum';

export interface SourceAlertConst {
  value: SourceAlertValues;
  label: string;
}

export const sourceAlertConst: SourceAlertConst[] = [
  {
    value: SourceAlertValues.EASYVISA,
    label: 'FORM.ALERTS.EASY_VISA'
  },
  {
    value: SourceAlertValues.USCIS,
    label: 'FORM.ALERTS.USCIS'
  },
  {
    value: SourceAlertValues.DHS,
    label: 'FORM.ALERTS.DHS'
  },
  {
    value: SourceAlertValues.DOS,
    label: 'FORM.ALERTS.DOS'
  },
  {
    value: SourceAlertValues.NVC,
    label: 'FORM.ALERTS.NVC'
  },
  {
    value: SourceAlertValues.AILA,
    label: 'FORM.ALERTS.AILA'
  },
  {
    value: SourceAlertValues.SSA,
    label: 'FORM.ALERTS.SSA'
  },
  {
    value: SourceAlertValues.SCOTUS,
    label: 'FORM.ALERTS.SCOTUS'
  },
  {
    value: SourceAlertValues.DOJ,
    label: 'FORM.ALERTS.DOJ'
  },
  {
    value: SourceAlertValues.IRS,
    label: 'FORM.ALERTS.IRS'
  },
  {
    value: SourceAlertValues.US_GOV,
    label: 'FORM.ALERTS.US_GOV'
  },
];
