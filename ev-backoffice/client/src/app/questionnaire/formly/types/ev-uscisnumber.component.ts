import { Component } from '@angular/core';

import { isEmpty } from 'lodash-es';

import { BaseFieldTypeComponent } from './base-fieldtype.component';

@Component({
  selector: 'app-formly-uscisnumber',
  template: `
    <div class="custom-uscisumber">
      <input
        type="text" min="0" class="form-control"
        [formlyAttributes]="field" [value]="uscisNumberModelValue"
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

      .custom-uscisumber input[type=text] {
        text-transform: uppercase;
      }

      .custom-uscisumber .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvUscisNumberComponent extends BaseFieldTypeComponent {
  // Notes: All CAPS, numbers, and space. No other characters allowed.
  // Format example: EAC 15 007 50156

  uscisNumberModelValue = '';

  initializeModelValue() {
    this.setPlaceholderInTemplateOptions();
    this.initUscisNumberModelValue();
  }

  initUscisNumberModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      this.uscisNumberModelValue = modelValue;
    }
  }

  onChangeHandler(e) {
    this.uscisNumberModelValue = e.target.value;
    const uscisNumber = this.uscisNumberModelValue + '';
    this.saveAnswer(uscisNumber.toUpperCase());
  }


  onPasteHandler(e) {
    const pastedData: string = e.clipboardData.getData('Text');
    const numericRegExp = new RegExp('^[A-Za-z\\d\\s]+$');
    // This is used to allow only alphanumberic and space from pastedData
    if (!numericRegExp.test(pastedData)) {
      e.preventDefault();
    }
  }


  onKeyPressHandler(e) {
    const code = e.keyCode;
    if ((code > 47 && code < 58) || // numeric (0-9)
      (code > 64 && code < 91) || // upper alpha (A-Z)
      (code > 96 && code < 123) || // lower alpha (A-Z)
      (code === 32)) { // space
      return true;
    }
    return false;
  }


  setPlaceholderInTemplateOptions() {
    const templateOptions = this.field.templateOptions;
    templateOptions.placeholder = 'example: EAC 15 007 50156';
  }
}
