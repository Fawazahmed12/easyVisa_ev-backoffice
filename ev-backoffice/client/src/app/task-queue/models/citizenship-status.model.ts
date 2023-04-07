import { CitizenshipStatus } from '../../core/models/citizenship-status.enum';

export interface CitizenshipStatusValue {
  fullLabel: string;
  label: string;
  value: CitizenshipStatus;
}

export const citizenshipStatusValue: CitizenshipStatusValue[]  = [
  {
    fullLabel: 'TEMPLATE.TASK_QUEUE.APPLICANT.US_CITIZEN',
    label: 'TEMPLATE.TASK_QUEUE.APPLICANT.US_CITIZEN_SHORT',
    value: CitizenshipStatus.U_S_CITIZEN,
  },
  {
    fullLabel: 'TEMPLATE.TASK_QUEUE.APPLICANT.LPR',
    label: 'TEMPLATE.TASK_QUEUE.APPLICANT.LPR_SHORT',
    value: CitizenshipStatus.LPR,
  },
  {
    fullLabel: 'TEMPLATE.TASK_QUEUE.APPLICANT.ALIEN',
    label: 'TEMPLATE.TASK_QUEUE.APPLICANT.ALIEN_SHORT',
    value: CitizenshipStatus.ALIEN,
  },
  {
    fullLabel: 'TEMPLATE.TASK_QUEUE.APPLICANT.US_NATIONAL',
    label: 'TEMPLATE.TASK_QUEUE.APPLICANT.US_NATIONAL_SHORT',
    value: CitizenshipStatus.U_S_NATIONAL,
  },
];
