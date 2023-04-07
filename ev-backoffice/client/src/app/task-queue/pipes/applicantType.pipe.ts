import { Pipe, PipeTransform } from '@angular/core';
import { ApplicantType } from '../../core/models/applicantType.enum';

@Pipe({name: 'applicantType'})
export class ApplicantTypePipe implements PipeTransform {
  transform(value: string) {
    switch (value) {
      case ApplicantType.PETITIONER: {
        return 'TEMPLATE.APPLICANTS.PETITIONER';
      }
      case ApplicantType.BENEFICIARY: {
        return 'TEMPLATE.APPLICANTS.BENEFICIARY';
      }
      case ApplicantType.PRINCIPAL_BENEFICIARY: {
        return 'TEMPLATE.APPLICANTS.PRINCIPAL_BENEFICIARY';
      }
      case ApplicantType.DERIVATIVE_BENEFICIARY: {
        return 'TEMPLATE.APPLICANTS.DERIVATIVE_BENEFICIARY';
      }
      case ApplicantType.PRIMARY_APPLICANT: {
        return 'TEMPLATE.APPLICANTS.PRIMARY_APPLICANT';
      }
      default: {
        return '';
      }
    }
  }
}
