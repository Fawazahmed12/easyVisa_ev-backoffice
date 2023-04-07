import { PackageStatus } from '../../../../core/models/package/package-status.enum';

export class SearchParams {
  benefitCategory: string | null = null;
  countries: string[] | null = null;
  closedDateStart: string | null = null;
  closedDateEnd: string | null = null;
  openedDateStart: string | null = null;
  openedDateEnd: string | null = null;
  lastAnsweredOnDateStart: string | null = null;
  lastAnsweredOnDateEnd: string | null = null;
  isOwed: boolean | null = null;
  petitionerStatus: string[] | null = null;
  states: string[] | null = null;
  status: PackageStatus[] | null = [PackageStatus.LEAD, PackageStatus.OPEN, PackageStatus.BLOCKED];
  easyVisaId: string | null = null;
  mobileNumber: string | null = null;
  lastName: string | null = null;
  representativeId: number;

  constructor(obj) {
    for (const field of Object.keys(obj)) {
      if (field !== undefined) {
        this[field] = obj[field];
      }
    }
  }
}
