import { Component, ViewChild } from '@angular/core';

import { BaseFieldTypeComponent } from './base-fieldtype.component';
import { ConfirmButtonType } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { EMPTY } from 'rxjs';
import { catchError } from 'rxjs/operators';

const VALUE_YES = 'yes';
const VALUE_NO = 'no';

@Component({
  selector: 'app-formly-conditionalresidencestatus',
  template: `
    <div class="custom-conditionalresidencestatus">
      <div *ngFor="let option of to.options let i = index" class="custom-control custom-radio">
        <input
          type="radio" class="custom-control-input"
          [id]="id+'_'+i"
          [name]="to.name"
          [formlyAttributes]="field"
          [value]="option.value"
          (change)="onChangeHandler($event)"
          [(ngModel)]="conditionalResidenceStatusModelValue"
          [disabled]="isDisabled()"
        >
        <label [for]="id+'_'+i" class="custom-control-label">{{ option.label }}</label>
      </div>
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>

    <ng-template #conditionalResidenceStatusModal>
      <div [innerHtml]="getConditionalResidenceStatusWarningText()"></div>
    </ng-template>
  `,
  styles: [
      `
      .custom-conditionalresidencestatus .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: 28px;
        z-index: 1;
      }
    `
  ]
})
export class EvConditionalResidenceStatusComponent extends BaseFieldTypeComponent {

  conditionalResidenceStatusModelValue = '';
  @ViewChild('conditionalResidenceStatusModal', { static: true }) conditionalResidenceStatusModal;

  initializeModelValue() {
    this.initConditionalResidenceStatusModelValue();
  }

  initConditionalResidenceStatusModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      this.conditionalResidenceStatusModelValue = modelValue;
    }
  }

  onChangeHandler(e) {
    if (this.conditionalResidenceStatusModelValue === VALUE_NO) {
      this.openConditionalResidenceStatusModal()
        .subscribe((data) => this.saveAnswer(this.conditionalResidenceStatusModelValue));
    } else {
      this.saveAnswer(this.conditionalResidenceStatusModelValue);
    }
  }

  openConditionalResidenceStatusModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];

    return this.modalService.openConfirmModal({
      header: 'Warning',
      body: this.conditionalResidenceStatusModal,
      buttons,
      centered: true,
      showCloseIcon: false,
      backdrop: 'static'
    }).pipe(
      catchError(() => {
        this.conditionalResidenceStatusModelValue = VALUE_YES;
        return EMPTY;
      })
    );
  }

  getConditionalResidenceStatusWarningText() {
    const attributes = this.getTemplateAttributes();
    return attributes.errorMessage;
  }

}
