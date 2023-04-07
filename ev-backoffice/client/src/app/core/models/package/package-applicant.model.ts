import { ApplicantType } from '../applicantType.enum';
import { Applicant } from '../applicant.model';
import { PetitionerStatus } from '../petitioner-status.enum';
import { ProcessRequestState } from '../process-request-state.enum';

export class PackageApplicant {
  id: number;
  fee?: number | null;
  benefitCategory?: string;
  inviteApplicant?: boolean;
  profile: Applicant;
  applicantType?: ApplicantType;
  citizenshipStatus: PetitionerStatus;
  optIn: ProcessRequestState;
  register: boolean;
  inOpenPackage: boolean;
  inBlockedPackage: boolean;
}

