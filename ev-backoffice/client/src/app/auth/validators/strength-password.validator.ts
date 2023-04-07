import { FormControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { measurePasswordStrength } from '../utils/measure-password-strength';

export const strengthPasswordValidator: ValidatorFn = (control: FormControl): ValidationErrors | null => {
   const passwordStrength = measurePasswordStrength(control.value).score;
   return (passwordStrength >= 3) ? null : { notStrength: true };
};

