import { Component } from '@angular/core';

import { BaseFieldTypeComponent } from './base-fieldtype.component';

@Component({
  selector: 'app-formly-bminumberinput',
  template: `
    <div class="custom-bminumberinput">
      <input
        type="number" class="form-control"
        [formlyAttributes]="field" [value]="bmiNumberInputModelValue"
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
      .custom-bminumberinput input[type=number]::-webkit-inner-spin-button,
      .custom-bminumberinput input[type=number]::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
      }

      .custom-bminumberinput .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: -256px;
        z-index: 1;
      }
    `
  ]
})
export class EvBmiNumberInputComponent extends BaseFieldTypeComponent {

  bmiNumberInputModelValue = '';

  initializeModelValue() {
    this.initBmiNumberInputModelValue();
  }

  initBmiNumberInputModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      this.bmiNumberInputModelValue = modelValue;
    }
  }

  onChangeHandler(e) {
    this.bmiNumberInputModelValue = e.target.value;
    this.saveAnswer(this.bmiNumberInputModelValue);
  }

  onPasteHandler(e) {
    const pastedData: string = e.clipboardData.getData('Text');
    const numericRegExp = new RegExp('^[0-9]+$');
    // This is used to allow only numbers from pastedData
    if (!numericRegExp.test(pastedData)) {
      e.preventDefault();
      return;
    }

    const maxCharLength: number = this.getMaxCharLength();
    const currentBmiNumberInputValue: string = e.target.value;
    const newBmiNumberInputValue: string = currentBmiNumberInputValue + '' + pastedData;
    const totalNumericInputLength = currentBmiNumberInputValue.length + pastedData.length;

    const maximumNumericValue: number = this.getMaxNumericValue();
    if (parseInt(newBmiNumberInputValue, 10) > maximumNumericValue) { // This is used to restrict the value if greater than the max-value
      e.preventDefault();
      this.bmiNumberInputModelValue = maximumNumericValue.toString();
    } else if (totalNumericInputLength >= maxCharLength) { // This is used to restrict the value if max number of character reached
      e.preventDefault();
      this.bmiNumberInputModelValue = newBmiNumberInputValue.substring(0, maxCharLength);
    }
  }

  getMaxCharLength(): number {
    const attributes = this.getTemplateAttributes();
    return +attributes.numericCharacterLength;
  }

  getMaxNumericValue(): number {
    const attributes = this.getTemplateAttributes();
    return +attributes.maximumNumericValue;
  }

  onKeyPressHandler(e) {
    // This is used to allow only numbers to be entered in a textbox
    if (e.keyCode > 31 && (e.keyCode < 48 || e.keyCode > 57)) {
      return false;
    }

    const currentBmiNumberInputValue: string = e.target.value;
    const newBmiNumberInputValue: string = currentBmiNumberInputValue + String.fromCharCode(e.keyCode);
    // This is used to restrict the value if greater than the max-value
    const maximumNumericValue: number = this.getMaxNumericValue();
    if (parseInt(newBmiNumberInputValue, 10) > maximumNumericValue) {
      return false;
    }

    // This is used to restrict the value if max number of character reached
    if (currentBmiNumberInputValue.length >= this.getMaxCharLength()) {
      return false;
    }
    return true;
  }
}
