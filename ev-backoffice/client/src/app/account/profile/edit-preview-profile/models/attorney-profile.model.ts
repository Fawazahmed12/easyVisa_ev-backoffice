import { Profile } from '../../../../core/models/profile.model';
import { OfficeAddress } from '../../../../core/models/officeAddress.model';
import { RegistrationStatus } from '../../../../core/models/registration-status.enum';
import { AttorneyType } from '../../../../core/models/attorney-type.enum';
import { RepresentativeType } from '../../../../core/models/representativeType.enum';
import { Organization } from '../../../../core/models/organization.model';
import { FeeSchedule } from '../../../../core/models/fee-schedule.model';

import { LicensedRegions } from './licensed-regions.model';
import { WorkingHours } from './working-hours.model';
import { Education } from './education.model';

export class AttorneyProfile extends Profile {
  officeEmail: string;
  officeAddress: OfficeAddress;
  registrationStatus: RegistrationStatus;
  attorneyType: AttorneyType;
  representativeType: RepresentativeType;
  faxNumber: string;
  officePhone: string;
  mobilePhone: string;
  websiteUrl: string;
  facebookUrl: string;
  linkedinUrl: string;
  twitterUrl: string;
  youtubeUrl: string;
  activeOrganizationId: number;
  organizations: Organization[];
  feeSchedule: FeeSchedule[];
  licensedRegions: LicensedRegions[];
  summary: string;
  awards: string;
  experience: string;
  profilePhoto: {url: string};
  workingHours: WorkingHours[];
  languages: string[];
  stateBarNumber: string;
  uscisOnlineAccountNo: string;
  practiceAreas: string[];
  education: Education[];

  // constructor(obj) {
  //   super(obj);
  // }

  // populateObject(obj) {
  //   for (const key in this) {
  //     if (typeof obj[key] !== 'undefined') {
  //       this[key] = obj[key];
  //     }
  //   }
  // }
}
