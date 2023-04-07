import { PetitionerStatus } from '../../core/models/petitioner-status.enum';

export interface PetitionerStatusValue {
  label: string;
  value: PetitionerStatus;
}

export const petitionerStatusValues: PetitionerStatusValue[]  = [
  {
    label: 'TEMPLATE.TASK_QUEUE.APPLICANT.US_CITIZEN',
    value: PetitionerStatus.U_S_CITIZEN,
  },
  {
    label: 'TEMPLATE.TASK_QUEUE.APPLICANT.LPR_SHOT',
    value: PetitionerStatus.LPR,
  },
  {
    label: 'TEMPLATE.TASK_QUEUE.APPLICANT.ALIEN',
    value: PetitionerStatus.ALIEN,
  },
  {
    label: 'TEMPLATE.TASK_QUEUE.APPLICANT.US_NATIONAL',
    value: PetitionerStatus.U_S_NATIONAL,
  },
];
