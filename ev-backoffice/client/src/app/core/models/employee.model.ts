import { Profile } from './profile.model';
import { Organization } from './organization.model';

export class Employee extends Profile {
  activeOrganizationId: number = null;
  organizations: Organization[] = [];

  constructor(obj) {
    super(obj);
    this.populateObject(obj);
  }
}
