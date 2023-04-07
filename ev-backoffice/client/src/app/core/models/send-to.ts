import { SendToValues } from './send-to.enum';

export interface SendToConst {
  value: SendToValues;
  label: string;
}

export const sendToConst: SendToConst[] = [
  {
    value: SendToValues.ACC_REPS,
    label: 'FORM.ALERTS.ACCREDITED_REP'
  },
  {
    value: SendToValues.ATTORNEYS,
    label: 'FORM.ALERTS.ATTORNEYS'
  },
  {
    value: SendToValues.EMPLOYEES,
    label: 'FORM.ALERTS.EMPLOYEES'
  },
  {
    value: SendToValues.CLIENTS,
    label: 'FORM.ALERTS.CLIENTS'
  },
  {
    value: SendToValues.EV_EMPLOYEES,
    label: 'FORM.ALERTS.EV_EMPLOYEES'
  }
];
