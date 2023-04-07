import {Component} from '@angular/core';

import {isEmpty} from 'lodash-es';

import {BaseFieldTypeComponent} from './base-fieldtype.component';

@Component({
  selector: 'app-formly-numericinput',
  template: `
    <div class="custom-numericinput">
      <input
        type="number" min="0" class="form-control"
        [formlyAttributes]="field" [value]="numericInputModelValue"
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
      .custom-numericinput input[type=number]::-webkit-inner-spin-button,
      .custom-numericinput input[type=number]::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
      }

      .custom-numericinput .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvNumberinputComponent extends BaseFieldTypeComponent {

  numericInputModelValue = '';
  textSelectionLength = 0;

  initializeModelValue() {
    this.initNumericInputModelValue();
  }

  initNumericInputModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[fieldKey];
    if (modelValue) {
      this.numericInputModelValue = modelValue;
    }
  }

  onChangeHandler(e) {
    this.numericInputModelValue = e.target.value;
    this.saveAnswer(this.numericInputModelValue);
  }

  onSelectHandler(e) {
    this.textSelectionLength = e.target.value.length;
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
    const currentNumericInputValue: string = e.target.value;
    const totalNumericInputLength = currentNumericInputValue.length + pastedData.length;
    // This is used to restrict the value if max number of character reached
    if (totalNumericInputLength >= maxCharLength) {
      e.preventDefault();
      const newNumericInputValue: string = currentNumericInputValue + '' + pastedData;
      this.numericInputModelValue = newNumericInputValue.substring(0, maxCharLength);
    }
  }

  getMaxCharLength(): number {
    const attributes = this.getTemplateAttributes();
    return +attributes.numericCharacterLength;
  }


  onKeyPressHandler(e) {

    // This is used to allow only numbers to be entered in a textbox
    if (e.keyCode > 31 && (e.keyCode < 48 || e.keyCode > 57)) {
      return false;
    }

    // This is used to allow to type the value while selecting the text within the text box
    if (this.textSelectionLength == this.getMaxCharLength()) {
      this.textSelectionLength = 0;
      return true;
    }

    // This is used to restrict the value if max number of character reached
    const currentNumericInputValue: string = e.target.value;
    if (currentNumericInputValue.length >= this.getMaxCharLength()) {
      return false;
    }
    return true;
  }
}
