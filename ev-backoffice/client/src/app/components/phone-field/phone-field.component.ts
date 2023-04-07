import { Component, forwardRef, Input } from '@angular/core';
import { ControlValueAccessor, FormControl, NG_VALUE_ACCESSOR } from '@angular/forms';

import { TranslateService } from '@ngx-translate/core';

import { find } from 'lodash-es';

import { PhoneCode, phoneCodes } from '../../core/models/phone-codes';

@Component({
  selector: 'app-phone-field',
  templateUrl: './phone-field.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => PhoneFieldComponent),
      multi: true
    }
  ]
})

export class PhoneFieldComponent implements ControlValueAccessor {

  @Input() label: string;
  @Input() primaryTextStyle = false;
  @Input() smallMarginStyle = false;
  @Input() registrationForm = false;
  @Input() col3Label = false;
  @Input() packageScreen = false;
  @Input() textAlignRight = false;

  phoneNumberControl = new FormControl();
  phoneCodeControl = new FormControl();
  phoneCodes: PhoneCode[] = phoneCodes;

  private phoneRegExp = /^\+?\(([0-9 .-]+)\)(.+)$/;

  onTouch: Function = () => {};

  private onChange: Function = (fullPhone: string) => {};

  constructor(
    private translateService: TranslateService,
  ) {
  }

  get phoneCodeFormatted() {
    return this.phoneCodeControl.value ? `(${this.phoneCodeControl.value})` : '';
  }

  get fullPhone() {
    const phoneNumberValue = this.phoneNumberControl.value ? `${this.phoneNumberControl.value}` : '';
    return `${this.phoneCodeFormatted}${phoneNumberValue}`;
  }

  get countryTooltip() {
    const currentCode: PhoneCode = find(this.phoneCodes, ['code', this.phoneCodeControl.value]);
    return currentCode ? `${currentCode.country} (${currentCode.code})` : this.translateService.instant('FORM.LABELS.SELECT_COUNTRY_CODE');
  }

  updateNumber() {
    this.onChange(this.fullPhone);
    this.onTouch();
  }

  writeValue(value = ''): void {
    const parsedValue = value && value.match(this.phoneRegExp) || [];
    const [fullNumber, parsedCode, parsedNumber] = parsedValue;
    const phoneCodeObject: PhoneCode = this.phoneCodes.find((el) => el.code === parsedCode) || null;
    const phoneNumber = phoneCodeObject ? parsedNumber : value;

    if (phoneCodeObject) {
      this.phoneCodeControl.patchValue(phoneCodeObject.code, {emitEvent: false});
    }
    this.phoneNumberControl.patchValue(phoneNumber, {emitEvent: false});
  }

  registerOnChange(fn: Function): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: Function): void {
    this.onTouch = fn;
  }

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.phoneCodeControl.disable({emitEvent: false});
      this.phoneNumberControl.disable({emitEvent: false});
    } else {
      this.phoneCodeControl.enable({emitEvent: false});
      this.phoneNumberControl.enable({emitEvent: false});
    }
  }
}
