import { Component } from '@angular/core';

import { isEmpty } from 'lodash-es';

import { BaseFieldTypeComponent } from './base-fieldtype.component';

@Component({
  selector: 'app-formly-alphanumeric',
  template: `
    <div class="custom-alphanumeric">
      <input
        type="text" min="0" class="form-control"
        [formlyAttributes]="field" [value]="alphaNumericModelValue"
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

      .custom-alphanumeric input[type=text] {
        text-transform: uppercase;
      }

      .custom-alphanumeric .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvAlphaNumericComponent extends BaseFieldTypeComponent {
  // Notes: All CAPS and also numbers only.

  alphaNumericModelValue = '';

  initializeModelValue() {
    this.initAlphaNumericModelValue();
  }

  initAlphaNumericModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      this.alphaNumericModelValue = modelValue;
    }
  }

  onChangeHandler(e) {
    this.alphaNumericModelValue = e.target.value;
    const alphaNumeric = this.alphaNumericModelValue + '';
    this.saveAnswer(alphaNumeric.toUpperCase());
  }


  onPasteHandler(e) {
    const pastedData: string = e.clipboardData.getData('Text');
    const numericRegExp = new RegExp('^[A-Za-z\\d]+$');
    // This is used to allow only alphanumberic from pastedData
    if (!numericRegExp.test(pastedData)) {
      e.preventDefault();
    }
  }


  onKeyPressHandler(e) {
    const code = e.keyCode;
    if ((code > 47 && code < 58) || // numeric (0-9)
      (code > 64 && code < 91) || // upper alpha (A-Z)
      (code > 96 && code < 123)) { // lower alpha (A-Z)
      return true;
    }
    return false;
  }
}
