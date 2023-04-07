import { Component, forwardRef, OnDestroy, OnInit } from '@angular/core';
import { ControlValueAccessor, FormControl, FormGroup, NG_VALUE_ACCESSOR } from '@angular/forms';

import { filter, map } from 'rxjs/operators';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { sourceAlertConst } from '../../../../core/models/source-alert';
import { merge } from 'rxjs';

@Component({
  selector: 'app-source-alert',
  templateUrl: './source-alert.component.html',
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => SourceAlertComponent),
      multi: true
    }
  ]
})
@DestroySubscribers()
export class SourceAlertComponent implements OnInit, OnDestroy, ControlValueAccessor {
  formGroup: FormGroup = new FormGroup({
    radioFieldsControl: new FormControl(),
    customFieldControl: new FormControl(),
  });

  customRadioValue = 'CUSTOM';

  onChange: (value: string) => void;
  onTouched: () => void;

  private subscribers: any = {};

  sourceAlertConst = sourceAlertConst;

  get radioFieldsControl() {
    return this.formGroup.get('radioFieldsControl');
  }

  get customFieldControl() {
    return this.formGroup.get('customFieldControl');
  }

  ngOnInit() {
    console.log(`${this.constructor.name} Init`);
  }

  addSubscribers() {
    this.subscribers.organizationIdSubscription = merge(
      this.radioFieldsControl.valueChanges,
      this.customFieldControl.valueChanges,
    ).pipe(
      filter(() => !!this.onChange),
      map((value) => {
        if (value === this.customRadioValue) {
          return this.customFieldControl.value;
        }
        return value;
      },
    )).subscribe((value) => this.onChange(value));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  writeValue(value) {
    const isCustomValue = !sourceAlertConst.some((sourceAlert) => sourceAlert.value === value);

    if (typeof value === 'undefined' || value === null) {
      this.customFieldControl.patchValue('', {emitEvent: false});
      this.radioFieldsControl.patchValue(null, {emitEvent: false});
    } else if (isCustomValue) {
      this.customFieldControl.patchValue(value, {emitEvent: false});
      this.radioFieldsControl.patchValue(this.customRadioValue, {emitEvent: false});
    } else {
      this.radioFieldsControl.patchValue(value, {emitEvent: false});
    }
  }

  registerOnChange(fn) {
    this.onChange = fn;
  }

  registerOnTouched(fn) {
    this.onTouched = fn;
  }

  setDisabledState(isDisabled: boolean) {
    isDisabled ?
      this.formGroup.disable({emitEvent: false})
      :
      this.formGroup.enable({emitEvent: false});
  }

}
