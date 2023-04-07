import {Component} from '@angular/core';

import {isEmpty} from 'lodash-es';

import {BaseFieldTypeComponent} from './base-fieldtype.component';

@Component({
  selector: 'app-formly-usciselisaccountnumber',
  template: `
    <div class="custom-usciselisaccountnumber">
      <input
        type="text" min="0" class="form-control"
        [formlyAttributes]="field" [value]="uscisElisAccountNumberModel"
        (keypress)="onKeyPressHandler($event)"
        (change)="onChangeHandler($event)"
        (paste)="onPasteHandler($event)"
        (select)="onSelectHandler($event)"
        [disabled]="isDisabled()"
      >
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>
  `,
  styles: [
    `
      .custom-usciselisaccountnumber .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvUscisElisAccountNumberComponent extends BaseFieldTypeComponent {
  // Notes: alphabets and numbers only and checking max character from attribute.

  uscisElisAccountNumberModel = '';
  textSelectionLength = 0;

  initializeModelValue() {
    this.initUscisElisAccountNumberModelValue();
  }

  initUscisElisAccountNumberModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[fieldKey];
    if (modelValue) {
      this.uscisElisAccountNumberModel = modelValue;
    }
  }

  onChangeHandler(e) {
    this.uscisElisAccountNumberModel = e.target.value;
    this.saveAnswer(this.uscisElisAccountNumberModel);
  }

  onSelectHandler(e) {
    this.textSelectionLength = e.target.value.length;
  }

  onPasteHandler(e) {
    const pastedData: string = e.clipboardData.getData('Text');
    const numericRegExp = new RegExp('^[A-Za-z\\d]+$');
    // This is used to allow only alphanumberic from pastedData
    if (!numericRegExp.test(pastedData)) {
      e.preventDefault();
      return;
    }

    const maxCharLength: number = this.getMaxCharLength();
    const currentAlphaNumericInputValue: string = e.target.value;
    const totalAlphaNumericInputLength = currentAlphaNumericInputValue.length + pastedData.length;
    // This is used to restrict the value if max number of character reached
    if (totalAlphaNumericInputLength >= maxCharLength) {
      e.preventDefault();
      const newAlphaNumericInputValue: string = currentAlphaNumericInputValue + '' + pastedData;
      this.uscisElisAccountNumberModel = newAlphaNumericInputValue.substring(0, maxCharLength);
    }
  }

  onKeyPressHandler(e) {
    // This is used to allow to type the value while selecting the text within the text box
    if (this.textSelectionLength == this.getMaxCharLength()) {
      this.textSelectionLength = 0
      return true;
    }

    // This is used to restrict the value if max number of character reached
    const currentAlphaNumericInputValue: string = e.target.value;
    if (currentAlphaNumericInputValue.length >= this.getMaxCharLength()) {
      return false;
    }

    const code = e.keyCode;
    if ((code > 47 && code < 58) || // numeric (0-9)
      (code > 64 && code < 91) || // upper alpha (A-Z)
      (code > 96 && code < 123)) { // lower alpha (A-Z)
      return true;
    }

    return false;
  }

  getMaxCharLength(): number {
    const attributes = this.getTemplateAttributes();
    return +attributes.inputCharacterLength;
  }
}
