import { Component } from '@angular/core';

import { isEmpty } from 'lodash-es';

import { BaseFieldTypeComponent } from './base-fieldtype.component';

@Component({
  selector: 'app-formly-textinput',
  template: `
    <div class="custom-textinput">
      <input
        type="text" min="0" class="form-control"
        [formlyAttributes]="field" [value]="textInputModelValue"
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
      `.custom-textinput .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvTextInputComponent extends BaseFieldTypeComponent {

  textInputModelValue = '';

  initializeModelValue() {
    this.initTextInputModelValue();
  }

  initTextInputModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      this.textInputModelValue = modelValue;
    }
  }

  onChangeHandler(e) {
    this.textInputModelValue = e.target.value;
    this.saveAnswer(this.textInputModelValue);
  }

  onPasteHandler(e) {
    const pastedData: string = e.clipboardData.getData('Text');
    const maxCharLength: number = this.getMaxCharLength();
    const currentTextInputValue: string = e.target.value;
    const totalTextInputLength = currentTextInputValue.length + pastedData.length;
    // This is used to restrict the value if max number of character reached
    if (totalTextInputLength >= maxCharLength) {
      e.preventDefault();
      const newTextInputValue: string = currentTextInputValue + '' + pastedData;
      this.textInputModelValue = newTextInputValue.substring(0, maxCharLength);
    }
  }

  getMaxCharLength(): number {
    const attributes = this.getTemplateAttributes();
    return +attributes.inputCharacterLength;
  }


  onKeyPressHandler(e) {
    // This is used to restrict the value if max number of character reached
    const currentTextInputValue: string = e.target.value;
    if (currentTextInputValue.length >= this.getMaxCharLength()) {
      return false;
    }
    return true;
  }
}
