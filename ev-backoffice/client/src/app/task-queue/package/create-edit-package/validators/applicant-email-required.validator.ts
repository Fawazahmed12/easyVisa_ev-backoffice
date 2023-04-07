import { FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';
import { ApplicantType } from '../../../../core/models/applicantType.enum';

export function requiredApplicantEmail(
  profileFormGroupName,
  emailControlName,
  inviteControlName,
  applicantTypeControlName,
): ValidatorFn {
  return (group: FormGroup): ValidationErrors | null => {
    const emailControl = group.get(profileFormGroupName).get(emailControlName);
    const inviteControlValue = group.get(inviteControlName).value;
    const applicantControlValue = group.get(applicantTypeControlName).value;
    return inviteControlValue && applicantControlValue !== ApplicantType.PETITIONER ? Validators.required(emailControl) : null;
  };
}
