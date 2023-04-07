import { PetitionerStatus } from '../petitioner-status.enum';
import { Applicant } from '../applicant.model';

export class Petitioner {
  petitionerStatus?: PetitionerStatus;
  aNumber: string;
  elisAccountNumber: string;
  profile: Applicant;
}
