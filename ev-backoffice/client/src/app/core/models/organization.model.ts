import { OrganizationType } from './organization-type.enum';
import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';

export class Organization {
  id: string;
  name: string;
  organizationType: OrganizationType;
  isAdmin: boolean;
  position: EmployeePosition;

  constructor(obj) {
    this.populateObject(obj);
  }

  populateObject(obj) {
    for (const key in this) {
      if (typeof obj[key] !== 'undefined') {
        this[key] = obj[key];
      }
    }
  }
}

