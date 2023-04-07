import { Component } from '@angular/core';

import { isEmpty } from 'lodash-es';

import { BaseFieldTypeComponent } from './base-fieldtype.component';

@Component({
  selector: 'app-formly-phonenumber',
  template: `
    <div class="custom-phonenumber">
      <input
        type="text" min="0" class="form-control"
        [formlyAttributes]="field" [value]="phoneNumberModelValue"
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
      .custom-phonenumber input[type=number]::-webkit-inner-spin-button,
      .custom-phonenumber input[type=number]::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
      }

      .custom-phonenumber .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvPhoneNumberComponent extends BaseFieldTypeComponent {

  phoneNumberModelValue = '';
  MAX_US_PHONE_NUMERIC_LENGTH = 10;

  initializeModelValue() {
    this.initPhoneNumberModelValue();
  }

  initPhoneNumberModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      this.phoneNumberModelValue = modelValue;
    }
  }

  onChangeHandler(e) {
    this.phoneNumberModelValue = e.target.value;
    this.saveAnswer(this.phoneNumberModelValue);
  }

  onPasteHandler(e) {
    const pastedData: string = e.clipboardData.getData('Text');
    const numericRegExp = new RegExp('^[0-9 ()+-]+$');
    // This is used to allow only numbers from pastedData
    if (!numericRegExp.test(pastedData)) {
      e.preventDefault();
      return;
    }

    const attributes = this.getTemplateAttributes();
    if (attributes.selectedCountry !== 'united_states') {
      return;
    }


    const currentPhoneNumberValue: string = e.target.value;
    const newPhoneNumberValue: string = currentPhoneNumberValue + '' + pastedData;
    const phoneNumberNumericFieldLength = this.getNumericCharacterCount(newPhoneNumberValue);
    // This is used to restrict the value if max number of character reached
    if (phoneNumberNumericFieldLength >= this.MAX_US_PHONE_NUMERIC_LENGTH) {
      e.preventDefault();
      this.phoneNumberModelValue = newPhoneNumberValue.substring(0, this.MAX_US_PHONE_NUMERIC_LENGTH);
    }
  }


  // If the selected Country is the United States, then the numeric field MUST have EXACTLY 10 digits.
  // However there is no limit to the digits allowed in any other country. Also, NO exponent letter ‘E’
  onKeyPressHandler(e) {
    // This is used to allow only numbers to be entered in a textbox
    if (this.isAllowedSpecialCharacters(e)) {
      return true;
    }

    // This is used to allow only numbers to be entered in a textbox
    if (!this.isNumericCharacters(e)) {
      return false;
    }

    // This is used to restrict the value if max number of character reached
    const currentPhoneNumberValue: string = e.target.value;
    const attributes = this.getTemplateAttributes();
    if (attributes.selectedCountry === 'united_states') {
      return this.validateUSPhoneNUmber(currentPhoneNumberValue);
    }
    return true;
  }

  // The field is a numeric field, but we also need to allow hyphens (-), Space (), Parentheses (()), plus (+)
  isNumericCharacters(e) {
    // This is used to allow only numbers to be entered in a textbox
    if (e.keyCode > 31 && (e.keyCode < 48 || e.keyCode > 57)) {
      return false;
    }
    return true;
  }

  // The field is a numeric field, but we also need to allow hyphens (-), Space (), Parentheses (()), plus (+)
  isAllowedSpecialCharacters(e) {
    const validKeyCodes = [
      43, // Plus (+) 43
      45, // Minus (-)	45
      32, // Space	32
      40, // Open.Parentheses 	40
      41, // Close.Parentheses 41
    ];
    return (validKeyCodes.indexOf(e.keyCode) !== -1) ? true : false;
  }


  // If the selected Country is the United States, then the numeric field MUST have EXACTLY 10 digits
  validateUSPhoneNUmber(currentPhoneNumberValue) {
    const phoneNumberNumericFieldLength = this.getNumericCharacterCount(currentPhoneNumberValue);
    if (phoneNumberNumericFieldLength >= this.MAX_US_PHONE_NUMERIC_LENGTH) {
      return false;
    }
    return true;
  }

  getNumericCharacterCount(currentPhoneNumberValue) {
    const numberPattern = /\d+/g;
    const parsedValue = currentPhoneNumberValue.match(numberPattern) || [];
    const currentPhoneNumberLength = 0;
    const phoneNumberNumericFieldLength = parsedValue.reduce((accumulator, currentValue) => accumulator + currentValue.length, currentPhoneNumberLength);
    return phoneNumberNumericFieldLength;
  }
}
