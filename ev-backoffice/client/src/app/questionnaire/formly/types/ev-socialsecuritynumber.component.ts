import { Component, AfterViewInit, ElementRef, ViewChild } from '@angular/core';

import { isEmpty } from 'lodash-es';

import { BaseFieldTypeComponent } from './base-fieldtype.component';

@Component({
  selector: 'app-formly-ssnumber',
  template: `
    <div class="custom-ssnumber">
      <span class="mask-label hyphen-1">-</span>
      <span class="mask-label hyphen-2">-</span>
      <input
        #ssnInputElem
        type="text" min="0" class="form-control"
        [formlyAttributes]="field" [ngModel]="ssNumberModelValue"
        mask="000-00-0000" [dropSpecialCharacters]="false"
        [disabled]="isDisabled()"
      >
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>
  `,
  styles: [
      `
      .custom-ssnumber .mask-label {
        display: inline-block;
        position: absolute;
        padding: 2px 0;
        color: #b2b2b2;
        top: 0;
        left:0;
        z-index: 1;
      }

      .custom-ssnumber .hyphen-1{
        left: 50px;
      }

      .custom-ssnumber .hyphen-2{
        left: 74px;
      }

      .custom-ssnumber input[type=number]::-webkit-inner-spin-button,
      .custom-ssnumber input[type=number]::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
      }

      .custom-ssnumber .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvSocialSecurityNumberComponent extends BaseFieldTypeComponent implements AfterViewInit{

  ssNumberModelValue = '';
  @ViewChild('ssnInputElem') ssnInputElem: ElementRef;

  // Question: Social Security Number (If any)
  // Notes: Dimmed out gray (40%) hyphens appear automatically after user types the
  //       3rd and 5th digits so that the result looks like this (845-48-6548)

  initializeModelValue() {
    this.initSSNumberModelValue();
  }

  ngAfterViewInit() {
    const listenerType: string = this.isSafari() ? 'blur' : 'change';
    this.ssnInputElem.nativeElement.addEventListener(listenerType, this.onChangeHandler.bind(this));
  }

  isSafari(): Boolean {
    return (navigator.userAgent.search('Safari') >= 0 && navigator.userAgent.search('Chrome') < 0);
  }

  initSSNumberModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue: string = this.field.model[ fieldKey ];
    if (modelValue) {
      this.ssNumberModelValue = this.getSocialSecurityDisplayText(modelValue);
    }
  }


  onChangeHandler(e) {
    this.ssNumberModelValue = e.target.value;
    const numericSSNumber = this.getSocialSecurityNumericValue(this.ssNumberModelValue);
    this.saveAnswer(numericSSNumber);
  }


  getSocialSecurityDisplayText(currentSSNumberValue: string): string {
    const numericSSNumber = this.getSocialSecurityNumericValue(currentSSNumberValue);
    if (numericSSNumber.length > 5) {
      return numericSSNumber.replace(/(\d{3})(\d{2})(\d+)/, '$1-$2-$3');
    } else if (numericSSNumber.length > 3) {
      return numericSSNumber.replace(/(\d{3})(\d+)/, '$1-$2');
    }
    return numericSSNumber;
  }


  getSocialSecurityNumericValue(currentSSNumberValue: string): string {
    const numericSSNumber = currentSSNumberValue.split('-').join('');
    return numericSSNumber;
  }
}
