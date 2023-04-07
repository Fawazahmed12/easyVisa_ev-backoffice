import { PetitionerStatus } from './petitioner-status.enum';
import { PackageStatus } from './package/package-status.enum';
import {pickBy, isEqual, size} from 'lodash-es';

export class CompareParams {
  benefitCategory: string[] = null;
  countries: string[] = null;
  closedDateStart: string = null;
  closedDateEnd: string = null;
  openedDateStart: string = null;
  openedDateEnd: string = null;
  lastAnsweredOnDateStart: string = null;
  lastAnsweredOnDateEnd: string = null;
  easyVisaId: string = null;
  isOwed: string = null;
  lastName: string = null;
  mobileNumber: string = null;
  petitionerStatus: PetitionerStatus = null;
  status: PackageStatus[] = null;
  states: string[]  = null;

  constructor(obj) {
    this.populateObject(obj);
  }

  populateObject?(obj) {
    for (const key in obj) {
      if (key in this) {
        this[key] = obj[key];
      }
    }
  }

  isDefaultParams() {
    const keysWithValues = pickBy(this, (v) => !!v && (typeof v !== 'function'));
    const isDefaultStatuses = isEqual([PackageStatus.LEAD, PackageStatus.OPEN, PackageStatus.BLOCKED], keysWithValues.status)
      && keysWithValues.status.length === 3;
    return isDefaultStatuses && size(keysWithValues) === 1;
  }
}

