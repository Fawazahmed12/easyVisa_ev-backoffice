import { Role } from './role.enum';
import { Attorney } from './attorney.model';
import { Applicant } from './applicant.model';

export class User {
  profile: Applicant | Attorney;
  accountLocked?: boolean;
  enabled: boolean;
  id: string;
  lastLogin: string;
  username?: string;
  roles: Role[];
  activeMembership: boolean;
  paid: boolean;
}

