import { Profile } from './profile.model';
import { OfficeAddress } from './officeAddress.model';

export interface Applicant extends Profile {
  homeNumber: string;
  mobileNumber: string;
  workNumber: string;
  homeAddress: OfficeAddress;
}
