import { Component } from '@angular/core';

import { isEmpty } from 'lodash-es';

import { BaseFieldTypeComponent } from './base-fieldtype.component';

@Component({
  selector: 'app-formly-i94number',
  template: `
    <div class="custom-i94number">
      <input
        type="text" min="0" class="form-control"
        mask="000-00000000" [dropSpecialCharacters]="false"
        [formlyAttributes]="field" [ngModel]="i94NumberModelValue"
        (keypress)="onKeyPressHandler($event)"
        (change)="onChangeHandler($event)"
        (paste)="onPasteHandler($event)"
        [disabled]="isDisabled()"
      >
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>
  `,
  styles: [
      `
      .custom-i94number input[type=number]::-webkit-inner-spin-button,
      .custom-i94number input[type=number]::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
      }

      .custom-i94number .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvI94RecordNumberComponent extends BaseFieldTypeComponent {

  i94NumberModelValue = '';

  initializeModelValue() {
    this.initI94NumberInputModelValue();
  }

  initI94NumberInputModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      this.i94NumberModelValue = this.getI94RecordNumberDisplayText(modelValue);
    }
  }

  onChangeHandler(e) {
    this.i94NumberModelValue = e.target.value;
    const i94Number = this.getI94RecordNumericValue(this.i94NumberModelValue);
    this.saveAnswer(i94Number);
  }


  onPasteHandler(e) {
    const pastedData: string = e.clipboardData.getData('Text');
    const numericRegExp = new RegExp('^[0-9]+$');
    // This is used to allow only numbers from pastedData
    if (!numericRegExp.test(pastedData)) {
      e.preventDefault();
      return;
    }

    const currentI94NumberValue: string = this.getI94RecordNumericValue(e.target.value);
    const totalI94NumberLength = currentI94NumberValue.length + pastedData.length;
    // This is used to restrict the value if max number of character reached
    if (totalI94NumberLength >= this.getMaxCharLength()) {
      e.preventDefault();
      const newI94NumberValue: string = currentI94NumberValue + '' + pastedData;
      this.i94NumberModelValue = newI94NumberValue.substring(0, this.getMaxCharLength());
    }
  }


  onKeyPressHandler(e) {
    // This is used to allow only numbers to be entered in a textbox
    if (e.keyCode > 31 && (e.keyCode < 48 || e.keyCode > 57)) {
      return false;
    }

    // This is used to restrict the value if max number of character reached
    const currentI94NumberValue: string = this.getI94RecordNumericValue(e.target.value);
    if (currentI94NumberValue.length >= this.getMaxCharLength()) {
      return false;
    }

    return true;
  }

  getI94RecordNumberDisplayText(currentI94RecordNumberValue: string): string {
    const numericSSNumber = this.getI94RecordNumericValue(currentI94RecordNumberValue);
    if (numericSSNumber.length > 3) {
      return numericSSNumber.replace(/(\d{3})(\d+)/, '$1-$2');
    }
    return numericSSNumber;
  }

  getI94RecordNumericValue(currentI94RecordNumberValue: string): string {
    const numericI94RecordNumber = currentI94RecordNumberValue.split('-').join('');
    return numericI94RecordNumber;
  }

  // Notes: 12 digits, 3, then hyphen, then 8 more digits
  getMaxCharLength(): number {
    const attributes = this.getTemplateAttributes();
    return +attributes.numericCharacterLength;
  }
}
