import { AfterViewInit, Component, ElementRef } from '@angular/core';
import { BaseFieldWrapperComponent } from './base-field-wrapper';
import { FocusManagerService } from '../../services/focusmanager.service';

@Component({
  selector: 'app-formly-horizontal-wrapper',
  template: `
    <div class="form-group row horizontal-wrapper">
      <label [attr.for]="id" class="col-sm-7 col-form-label" *ngIf="to.label">
        {{ to.label }}
        <ng-container *ngIf="to.required && to.hideRequiredMarker !== true">*</ng-container>
        <span
          *ngIf="canShowToolTip()"
          class="help-text"
          [placement]="['bottom', 'auto']"
          [ngbTooltip]="tipContent"
          [tooltipClass]="getTooltipClass()"
          [closeDelay]="getTooltipCloseDelay()"
          container="body"
        >
            Help
        </span>
      </label>
      <div class="col-sm-5">
        <ng-template #fieldComponent></ng-template>
      </div>
      <span class="icon-container">
        <i class="fa tick-icon" *ngIf="hasQuestionAnswered()">&#10003;</i>
        <i class="fa minus-icon" *ngIf="!hasQuestionAnswered()">&#8722;</i>
      </span>
    </div>

    <ng-template #tipContent>
      <div [innerHtml]="getToolTipContent()"></div>
    </ng-template>
  `,
  styles: [
      `
      .form-group {
        margin-bottom: 5px;
      }

      .help-text {
        color: #006cb7;
        padding: 5px;
      }

      .horizontal-wrapper {
        position: relative;
        margin: 5px 10px;
      }

      .icon-container {
        position: absolute;
        top: 0;
        right: 30px;
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
        font-weight: bold;
      }
    `
  ]
})
export class FormlyHorizontalWrapperComponent extends BaseFieldWrapperComponent implements AfterViewInit {

  constructor(private focusManagerService: FocusManagerService,
              private element: ElementRef) {
    super();
  }


  ngAfterViewInit() {
    const inputEl = this.element.nativeElement.querySelector('input');
    if (this.focusManagerService.isActiveField(this.field) && inputEl) {
      inputEl.focus();
    }
  }

  getToolTipContent() {
    const toolTip = this.getToolTip();
    const toolTipContent = toolTip.replace('EV_IMG_PATH', '../../../../assets/images/questionnaire');
    return toolTipContent;
  }

  getTooltipClass() {
    const attributes = this.getTemplateAttributes();
    return `${attributes.tooltipClass} questionnaire-tooltip` || 'questionnaire-tooltip';
  }

  getTooltipCloseDelay() {
    const attributes = this.getTemplateAttributes();
    return attributes.tooltipCloseDelay || 0;
  }
}
