import { FormControl, ValidationErrors, ValidatorFn, Validators } from '@angular/forms';

export const EVIdEmailValidator: ValidatorFn = (control: FormControl): ValidationErrors | null => {

  const NUMBER_REGEXP = /(^[A-Z]\d{10}$)/;

  if (!NUMBER_REGEXP.test(control.value)) {
    return Validators.email(control) ? {invalid: true} : null;
  } else {
    return null;
  }
};
