import { RepresentativeType } from '../../core/models/representativeType.enum';
import { Profile } from '../../core/models/profile.model';

export const mockAttorneyProfile: Profile | any = {
  firstName: 'First',
  lastName: 'last',
  middleName: 'middle',
  username: 'packageattorney',
  easyVisaId: 'A0000010042',
  email: 'registeredAttorney@easyvisa.com',
  activePackageId: 9,
  officeEmail: 'registeredAttorney@easyvisa.com',
  officeAddress: null,
  registrationStatus: 'NEW',
  attorneyType: null,
  representativeType: RepresentativeType.ATTORNEY,
  id: 232,
  faxNumber: null,
  officePhone: null,
  mobilePhone: '99999123123',
  websiteUrl: null,
  facebookUrl: null,
  linkedinUrl: null,
  twitterUrl: null,
  youtubeUrl: null,
  activeOrganizationId: 50,
  organizations: [{
      id: 50,
      isAdmin: false,
      name: 'Solo',
      organizationType: 'SOLO_PRACTICE',
    }],
  feeSchedule: []
};
