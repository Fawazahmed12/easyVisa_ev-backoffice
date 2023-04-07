import { Component, ViewChild } from '@angular/core';

import { BaseFieldTypeComponent } from './base-fieldtype.component';
import {AnswerValidationModel} from '../../models/questionnaire.model';

@Component({
  selector: 'app-formly-custom-ngselect',
  template: `
    <div class="form-group custom-ngselect">
      <div class="input-group">
        <ng-select
          [items]="to.options"
          bindLabel="label"
          bindValue="value"
          [searchable]="true"
          [clearable]="false"
          placeholder="--Select--"
          [(ngModel)]="ngSelectModelValue"
          (change)="onNgSelectChange($event)"
          (focus)="onEnableFocusEvent($event)"
          (blur)="onDisableFocusEvent($event)"
          [selectOnTab]="true"
          [disabled]="isDisabled()"
          #s
        >
        </ng-select>
      </div>
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>
  `,
  styles: [ `

    .custom-ngselect {
      margin: 0;
    }

    .custom-ngselect .input-group {
      width: 85%;
    }

    .custom-ngselect .spinner-container {
      display: inline-block;
      position: absolute;
      top: -1px;
      right: 28px;
      z-index: 1;
    }
  `
  ]
})
export class EvNgSelectComponent extends BaseFieldTypeComponent {

  ngSelectModelValue;
  @ViewChild('s', { static: true }) s: any;

  initializeModelValue() {
    this.initNgSelectModelValue();
  }

  initNgSelectModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    this.setSelectModelValue(modelValue);

    if (this.field[ 'opened' ]) {
      this.s.open();
      this.focusManagerService.setActiveFieldPath(null);
    }
  }

  onNgSelectChange(e) {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue !== this.ngSelectModelValue) {
      this.saveAnswer(this.ngSelectModelValue);
      this.focusManagerService.setActiveFieldPath(null);
    }
  }

  onEnableFocusEvent(e) {
    const activePath = this.focusManagerService.findActiveFieldPath(this.field);
    this.focusManagerService.setActiveFieldPath(activePath);
  }

  onDisableFocusEvent(e) {
    this.focusManagerService.setActiveFieldPath(null);
  }

  resetModelValue(resetValue) {
    this.setSelectModelValue(resetValue);
  }

  private setSelectModelValue(modelValue: string) {
    if (modelValue) {
      this.ngSelectModelValue = modelValue;
    }
  }
}
