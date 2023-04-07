import { Component, forwardRef, Input } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

import { Petitioner } from '../../../../core/models/package/petitioner.model';
import { PackageApplicant } from '../../../../core/models/package/package-applicant.model';

@Component({
  selector: 'app-edit-amount-owed',
  templateUrl: 'edit-amount-owed.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => EditAmountOwedComponent),
      multi: true
    }
  ],
})
export class EditAmountOwedComponent implements ControlValueAccessor {

  @Input() owed = 0;

  @Input() petitioner: Petitioner;

  @Input() applicants: PackageApplicant[];

  value: number;

  disabled: boolean;

  onChange = (value) => {};
  onTouched = () => {};

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled) {
    this.disabled = isDisabled;
  }

  writeValue(value) {
    this.value = value;
  }
}
