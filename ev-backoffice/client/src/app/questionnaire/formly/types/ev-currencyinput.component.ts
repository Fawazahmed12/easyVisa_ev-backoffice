import { AfterViewInit, Component, ElementRef, ViewChild } from '@angular/core';

import { isEmpty } from 'lodash-es';

import { BaseFieldTypeComponent } from './base-fieldtype.component';

@Component({
  selector: 'app-formly-currencyinput',
  template: `
    <div class="custom-currencyinput">
      <span class="currency-label">$</span>
      <input
        #currencyInputElem
        type="text" min="0" class="form-control"
        mask="separator" thousandSeparator=","
        [dropSpecialCharacters]="false"
        [formlyAttributes]="field" [ngModel]="currencyInputModelValue"
        (keypress)="onKeyPressHandler($event)"
        [disabled]="isDisabled()"
      >
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>
  `,
  styles: [
    `
      .custom-currencyinput .currency-label {
        display: inline-block;
        position: absolute;
        padding: 2px 8px;
        color: #a5a5a5;
        top: 0;
        left: 15px;
      }

      .custom-currencyinput .form-control {
        padding-left: 18px;
      }

      .custom-currencyinput input::placeholder {
        color: lightgray;
      }

      .custom-currencyinput input[type=number]::-webkit-inner-spin-button,
      .custom-currencyinput input[type=number]::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
      }

      .custom-currencyinput .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvCurrencyInputComponent extends BaseFieldTypeComponent implements AfterViewInit {

  currencyInputModelValue = '';
  @ViewChild('currencyInputElem') currencyInputElem: ElementRef;

  initializeModelValue() {
    this.initCurrencyInputModelValue();
  }

  initCurrencyInputModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      this.currencyInputModelValue = modelValue;
    }
  }

  ngAfterViewInit() {
    const listenerType: string = this.isSafari() ? 'blur' : 'change';
    this.currencyInputElem.nativeElement.addEventListener(listenerType, this.onChangeHandler.bind(this));
  }

  isSafari(): Boolean {
    return (navigator.userAgent.search('Safari') >= 0 && navigator.userAgent.search('Chrome') < 0);
  }

  onChangeHandler(e) {
    const currencyInput = this.getCurrencyValueWithoutSeparator(e.target.value);
    if(currencyInput!=this.currencyInputModelValue) {
      this.currencyInputModelValue = e.target.value;
      this.saveAnswer(currencyInput);
    }
  }

  onKeyPressHandler(e) {
    // This is used to allow only numbers to be entered in a textbox
    if (e.keyCode > 31 && (e.keyCode < 48 || e.keyCode > 57)) {
      return false;
    }
    return true;
  }

  getCurrencyValueWithoutSeparator(currencyInputValue: string): string {
    const currencyValueWithoutSeparator = currencyInputValue.split(',').join('');
    return currencyValueWithoutSeparator;
  }
}
