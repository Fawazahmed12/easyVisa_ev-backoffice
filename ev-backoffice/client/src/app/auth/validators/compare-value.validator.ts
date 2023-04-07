import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

export function compareValueValidator(controlName, comparedControlName): ValidatorFn {
  return (group: FormGroup): ValidationErrors | null => {
    const control = group.get(controlName);
    const comparedControl = group.get(comparedControlName);
    return control.value === comparedControl.value ? null : {mismatch: true};
  };
}
