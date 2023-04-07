import {Component} from '@angular/core';

import {isEmpty} from 'lodash-es';

import {BaseFieldTypeComponent} from './base-fieldtype.component';

@Component({
  selector: 'app-formly-aliennumber',
  template: `
    <div class="custom-aliennumber">
      <span class="alien-label">A-</span>
      <input
        type="number" min="0" class="form-control"
        [formlyAttributes]="field" [value]="alienNumberModelValue"
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
      .custom-aliennumber .alien-label {
        display: inline-block;
        position: absolute;
        padding: 2px 8px;
        color: #a5a5a5;
        top: 0;
        left: 15px;
      }

      .custom-aliennumber .form-control {
        padding-left: 30px;
      }

      .custom-aliennumber input::placeholder {
        color: lightgray;
      }

      .custom-aliennumber input[type=number]::-webkit-inner-spin-button,
      .custom-aliennumber input[type=number]::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
      }

      .custom-aliennumber .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvAlienNumberComponent extends BaseFieldTypeComponent {

  alienNumberModelValue = '';
  MAX_CHAR_LENGTH = 9;
  textSelectionLength = 0;

  initializeModelValue() {
    this.setPlaceholderInTemplateOptions();
    this.initAlienNumberModelValue();
  }

  initAlienNumberModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[fieldKey];
    if (modelValue) {
      this.alienNumberModelValue = modelValue;
    }
  }

  onChangeHandler(e) {
    this.alienNumberModelValue = e.target.value;
    const alienNumber = this.alienNumberModelValue + '';
    this.saveAnswer(alienNumber);
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

    const currentAlienNumberValue: string = e.target.value;
    const totalAlienNumberLength = currentAlienNumberValue.length + pastedData.length;
    // This is used to restrict the value if max number of character reached
    if (totalAlienNumberLength >= this.MAX_CHAR_LENGTH) {
      e.preventDefault();
      const newAlienNumberValue: string = currentAlienNumberValue + '' + pastedData;
      this.alienNumberModelValue = newAlienNumberValue.substring(0, this.MAX_CHAR_LENGTH);
    }
  }


  onKeyPressHandler(e) {
    // This is used to allow only numbers to be entered in a textbox
    if (e.keyCode > 31 && (e.keyCode < 48 || e.keyCode > 57)) {
      return false;
    }
    // This is used to allow to type the value while selecting the text within the text box
    if (this.textSelectionLength == this.MAX_CHAR_LENGTH) {
      this.textSelectionLength = 0;
      return true;
    }
    // This is used to restrict the value if max number of character reached
    const currentAlienNumberValue: string = e.target.value;
    if (currentAlienNumberValue.length >= this.MAX_CHAR_LENGTH) {
      return false;
    }
    return true;
  }

  setPlaceholderInTemplateOptions() {
    const templateOptions = this.field.templateOptions;
    templateOptions.placeholder = 'Enter numbers only';
  }
}
