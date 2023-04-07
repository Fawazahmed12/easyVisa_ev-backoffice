import { OrganizationType } from '../../../../core/models/organization-type.enum';
import { OfficeAddress } from '../../../../core/models/officeAddress.model';
import { WorkingHours } from './working-hours.model';

export class OrganizationProfile {
  id: number;
  name: string;
  organizationType: OrganizationType;
  summary: string;
  awards: string;
  experience: string;
  officeAddress: OfficeAddress;
  officePhone: string;
  mobilePhone: string;
  faxNumber: string;
  email: string;
  facebookUrl: string;
  linkedinUrl: string;
  twitterUrl: string;
  youtubeUrl: string;
  websiteUrl: string;
  yearFounded: number;
  organizationId: string;
  languages: string[];
  practiceAreas: string[];
  workingHours: WorkingHours[];
  profilePhoto: {url: string};
  easyVisaId: string;
}
