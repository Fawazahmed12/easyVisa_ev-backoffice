import {PackageApplicant} from './package-applicant.model';
import {Attorney} from '../attorney.model';
import {FileInfo} from '../file-info.model';
import {OrganizationType} from '../organization-type.enum';

import {PackageStatus} from './package-status.enum';
import {QuestionnaireSyncStatus} from './questionnaire-sync-status.enum';
import {Petitioner} from './petitioner.model';
import {Profile} from '../profile.model';
import {OrganizationProfile} from '../../../account/profile/edit-preview-profile/models/organization-profile.model';

export class Package {
  id: number;
  inviteApplicantEmailId: number;
  petitioner: Petitioner;
  status: PackageStatus;
  questionnaireSyncStatus: QuestionnaireSyncStatus;
  representative: Attorney | any;
  representativeId: string;
  applicants: PackageApplicant[];
  retainerAgreement: FileInfo;
  organization: {
    name: string;
    id: string;
    organizationType: OrganizationType;
    memberOf: string;
  };
  owed: number;
  assignees: Attorney[];
  creationDate: string;
  welcomeEmailId: number;
  welcomeEmailSentOn: string;
  title: string;
  categories: string;
  easyVisaId: number;
  lastActiveOn: string;
  documentCompletedPercentage: number;
  questionnaireCompletedPercentage: number;
  transferredOn: string;
  transferredBy: Profile;
  transferredAttorneyTo: Attorney;
  transferredOrganizationTo: {
    name: string;
    id: string;
    organizationType: OrganizationType;
    memberOf: string;
  };
}
