import { FormGroup, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export function requiredForPetitionerValidator(controlName, isRequired): ValidatorFn {
  return (group: FormGroup): ValidationErrors | null => {
    const control = group.get(controlName);
    return isRequired ? Validators.required(control) : null;
  };
}
