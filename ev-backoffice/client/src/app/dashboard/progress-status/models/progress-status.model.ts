import { ApplicantType } from '../../../core/models/applicantType.enum';

export class ProgressStatus {
  id: number;
  name: string;
  applicantType: ApplicantType;
  packageStatus: string;
  percentComplete: number;
  elapsedDays: number;
  totalDays: number;
  dateStarted: string;
  dateCompleted: string;
}
