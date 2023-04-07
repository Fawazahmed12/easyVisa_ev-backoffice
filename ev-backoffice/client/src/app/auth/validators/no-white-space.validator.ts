import { FormControl, ValidationErrors, ValidatorFn } from '@angular/forms';

export const noWhitespaceValidator: ValidatorFn = (control: FormControl): ValidationErrors | null => {
  const isWhitespace = !!control.value ? (control.value).trim().length === 0 : false;
  return !isWhitespace ? null : { required: true };
};
