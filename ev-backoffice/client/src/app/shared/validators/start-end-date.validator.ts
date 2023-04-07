import { FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { getFullDate } from '../utils/get-full-date';

export function startEndDateValidator(startDateControlName: string, endDateControlName: string): ValidatorFn {
  return (formGroup: FormGroup): ValidationErrors | null => {

    const startControlValue: string = formGroup.get(`${startDateControlName}`).value;
    const endControlValue: string = formGroup.get(`${endDateControlName}`).value;
    const currentDate = getFullDate(new Date());
    const selectedStartDate = getFullDate(new Date(startControlValue));
    const selectedEndDate = endControlValue ? getFullDate(new Date(endControlValue)) : currentDate;

    return (selectedStartDate > currentDate || selectedEndDate > currentDate) ?
      {invalidDateAfter: true} : selectedStartDate > selectedEndDate ?
      {invalidDateBefore: true} : null;
  };
}
