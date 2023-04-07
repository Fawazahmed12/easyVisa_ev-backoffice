import { EmployeeStatusValues } from './employee-status.enum';

export interface EmployeeStatus {
  label: string;
  value: EmployeeStatusValues;
}

export const employeeStatus: EmployeeStatus[] = [
  {
    label: 'Active',
    value: EmployeeStatusValues.ACTIVE,
  },
  {
    label: 'Inactive',
    value: EmployeeStatusValues.INACTIVE,
  },
  {
    label: 'Pending',
    value: EmployeeStatusValues.PENDING,
  },
];
