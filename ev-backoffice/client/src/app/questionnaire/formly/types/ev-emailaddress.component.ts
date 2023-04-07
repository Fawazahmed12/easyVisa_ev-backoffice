import { Component } from '@angular/core';

import { BaseFieldTypeComponent } from './base-fieldtype.component';

@Component({
  selector: 'app-formly-emailaddress',
  template: `
    <div class="custom-emailaddress">
      <input
        type="text" min="0" class="form-control"
        [formlyAttributes]="field" [value]="emailAddressModelValue"
        (change)="onChangeHandler($event)"
        [disabled]="isDisabled()"
      >
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>
  `,
  styles: [
      `
      .custom-emailaddress input[type=number]::-webkit-inner-spin-button,
      .custom-emailaddress input[type=number]::-webkit-outer-spin-button {
        -webkit-appearance: none;
        margin: 0;
      }

      .custom-emailaddress .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvEmailAddressComponent extends BaseFieldTypeComponent {

  emailAddressModelValue = '';

  initializeModelValue() {
    this.initEmailAddressModelValue();
  }

  initEmailAddressModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      this.emailAddressModelValue = modelValue;
    }
  }

  // https://www.w3resource.com/javascript/form/email-validation.php
  onChangeHandler(e) {
    this.emailAddressModelValue = e.target.value;
    const mailFormat = /^\w+([\.(-|+)|+]?\w+)*@\w{2,}([\.(-|+)]?\w+)*(\.\w{2,3})+$/;
    if (!this.emailAddressModelValue.match(mailFormat)) {
      const attributes = this.getTemplateAttributes();
      attributes.hasQuestionAnswered = 'false';
      this.modalService.showErrorModal(attributes.errorMessage);
    } else {
      this.saveAnswer(this.emailAddressModelValue);
    }
  }
}
