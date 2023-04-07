import { EmployeeStatusValues } from './employee-status.enum';
import { EmployeePosition } from './employee-position.enum';
import { Role } from '../../../core/models/role.enum';

export class OrganizationEmployee {
  id: number;
  employeeId: number;
  isAdmin: boolean;
  position: EmployeePosition;
  activeDate: string;
  inactiveDate?: string;
  mobilePhone?: string;
  officePhone: string;
  status: EmployeeStatusValues;
  workEmail?: string;
  profile: OrganizationEmployeeProfileData;
  roles: Role[];
}

export interface OrganizationEmployeeProfileData {
  id: number;
  easyVisaId: string;
  email: string;
  firstName: string;
  lastName: string;
  middleName?: string;
}
