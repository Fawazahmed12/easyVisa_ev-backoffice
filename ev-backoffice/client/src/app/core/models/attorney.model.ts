import { Profile } from './profile.model';
import { OfficeAddress } from './officeAddress.model';
import { RegistrationStatus } from './registration-status.enum';
import { Organization } from './organization.model';
import { RepresentativeType } from './representativeType.enum';
import { AttorneyType } from './attorney-type.enum';
import { FeeSchedule } from './fee-schedule.model';
import { EmployeeStatusValues } from '../../account/permissions/models/employee-status.enum';

export class Attorney extends Profile {
  activeOrganizationId: string = null;
  attorneyType?: AttorneyType = null;
  facebookUrl: string = null;
  faxNumber: string = null;
  feeSchedule: FeeSchedule[] = null;
  linkedinUrl: string = null;
  mobilePhone: string | number = null;
  officeAddress: OfficeAddress = null;
  officeEmail: string = null;
  officePhone: string = null;
  organizations: Organization[] = null;
  registrationStatus: RegistrationStatus = null;
  representativeType: RepresentativeType = null;
  twitterUrl: string = null;
  websiteUrl: string = null;
  youtubeUrl: string = null;
  creditBalance: number = null;
  balance: number = null;

  constructor(obj) {
    super(obj);
    this.populateObject(obj);
  }

}

export class AttorneyMenu {
  firstName: string;
  middleName?: string;
  lastName: string;
  id: number;
  status: EmployeeStatusValues;
  userId: number;
}

