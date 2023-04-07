import { Component } from '@angular/core';
import { BaseFieldWrapperComponent } from './base-field-wrapper';

@Component({
  selector: 'app-formly-vertical-wrapper',
  template: `
    <div class="form-group vertical-wrapper">
      <label [attr.for]="id" class="col-form-label" *ngIf="to.label">
        {{ to.label }}
        <ng-container *ngIf="to.required && to.hideRequiredMarker !== true">*</ng-container>
        <span
          *ngIf="canShowToolTip()"
          class="help-text"
          [placement]="['bottom', 'auto']"
          ngbTooltip="{{getToolTip()}}"
          tooltipClass="questionnaire-tooltip"
          container="body"
        >
          Help
        </span>
      </label>
      <ng-template #fieldComponent></ng-template>
      <span class="icon-container">
        <i class="fa fa-check blue" *ngIf="hasQuestionAnswered()"></i>
        <i class="fa fa-minus red" *ngIf="!hasQuestionAnswered()"></i>
      </span>
    </div>
  `,
  styles: [
      `
      .help-text {
        margin-left: 5px;
        color: #006cb7;
      }

      .vertical-wrapper {
        position: relative;
      }

      .icon-container {
        position: absolute;
        top: 5px;
        right: 0;
      }

      .icon-container .red {
        color: red;
      }

      .icon-container .blue {
        color: #006cb7;
      }
    `
  ]
})
export class FormlyVerticalWrapperComponent extends BaseFieldWrapperComponent {
  constructor() {
    super();
  }
}
