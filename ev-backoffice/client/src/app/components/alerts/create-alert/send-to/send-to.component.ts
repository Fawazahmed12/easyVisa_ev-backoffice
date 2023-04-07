import { Component, forwardRef } from '@angular/core';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

import { sendToConst } from '../../../../core/models/send-to';

@Component({
  selector: 'app-send-to',
  templateUrl: './send-to.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SendToComponent),
      multi: true
    }
  ]
})
export class SendToComponent implements ControlValueAccessor {

  value = [];
  disabled = false;

  onChange: (value: string[]) => void;
  onTouched: () => void;

  sendToConst = sendToConst;

  onCheckChange(checkValue) {
    const isValue = this.value.some((item) => item === checkValue);
    if (isValue) {
      this.value = this.value.filter((item) => item !== checkValue);
      this.onChange(this.value);
    } else if (!isValue) {
      this.value = [...this.value, checkValue];
      this.onChange(this.value);
    }
  }

  writeValue(value) {
    this.value = value;
  }

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean) {
    this.disabled = isDisabled;
  }
}
