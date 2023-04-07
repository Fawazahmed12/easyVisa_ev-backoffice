import { Package } from '../../core/models/package/package.model';
import { ApplicantType } from '../../core/models/applicantType.enum';

export function getPetitioner(item: Package) {
  const isUsualPackage = item.applicants.find(
    (applicant) => applicant.applicantType === ApplicantType.PETITIONER);

  if (!!isUsualPackage) {
    return isUsualPackage;
  }
  return item.applicants.find(
    (applicant) => applicant.applicantType === ApplicantType.BENEFICIARY);
}
