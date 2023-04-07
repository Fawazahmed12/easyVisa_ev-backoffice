import { Component } from '@angular/core';
import { BaseFieldWrapperComponent } from '../../base-field-wrapper';

@Component({
  selector: 'app-race',
  template: `
    <div class="race row">
      <label [attr.for]="id" class="col-sm-5 col-form-label" *ngIf="to.label">
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
      <div class="col-sm-7">
        <ng-template #fieldComponent></ng-template>
      </div>

      <span class="icon-container">
        <i class="fa tick-icon" *ngIf="hasQuestionAnswered()">&#10003;</i>
        <i class="fa minus-icon" *ngIf="!hasQuestionAnswered()">&#8722;</i>
      </span>
    </div>
  `,
  styles: [
      `
      .race {
        margin: 0 10px;
      }

      .help-text {
        margin-left: 5px;
        color: #006cb7;
        padding: 5px;
      }

      .icon-container {
        position: absolute;
        top: 0;
        right: 38px;
      }

      .icon-container .minus-icon {
        color: red;
        font-family: Roboto;
        font-size: 20px;
        font-weight: bold;
      }

      .icon-container .tick-icon {
        color: #003466;
        font-family: Wingdings;
        font-size: 18px;
        font-weight: bolder;
      }
    `
  ]
})
export class RaceWrapperComponent extends BaseFieldWrapperComponent {
  constructor() {
    super();
  }
}
