import { Profile } from '../../../../core/models/profile.model';
import { Organization } from '../../../../core/models/organization.model';
import { WorkingHours } from './working-hours.model';

export interface EmployeeProfile extends Profile {
  activeOrganizationId: number;
  organizations: Organization[];
  profilePhoto: {url: string};
  languages?: string[];
  workingHours?: WorkingHours[];
  populateObject(obj: any): any;
}
