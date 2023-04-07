import {Component, ViewChild} from '@angular/core';

import {NgbCalendar, NgbDate, NgbDateParserFormatter, NgbDatepickerConfig, NgbDateStruct} from '@ng-bootstrap/ng-bootstrap';

import {BaseFieldTypeComponent} from './base-fieldtype.component';
import {QuestionnaireService} from '../../services';
import {FocusManagerService} from '../../services/focusmanager.service';
import {ModalService} from '../../../core/services';
import {EvNgbDateParserFormatterService} from '../utils/ev-datepicker-parser-formatter.service';

const NO_MAX_DATE = 'NoMaxDate';

@Component({
  selector: 'app-formly-custom-datepicker',
  template: `
    <div class="form-group custom-datepicker">
      <div class="input-group">
        <input
          class="form-control"
          [formlyAttributes]="field"
          [(ngModel)]="dateModelValue" name="dp" ngbDatepicker #d="ngbDatepicker"
          (dateSelect)="onDateSelectHandler(d)"
          (keydown)="onKeyDownHandler($event)"
          (paste)="onPasteHandler($event)"
          (closed)="onCloseHandler()"
          [disabled]="isDisabled()"
          [autoClose]="'outside'"
          [container]="'body'"
        >
        <span class="close-icon" (click)="clearDate()" *ngIf="dateModelValue && isDisabled()">
          &times;
        </span>
        <div class="input-group-append">
          <button
            class="btn text-secondary calendar fa fa-calendar border-gray border-left-0 bg-white"
            (click)="onCalendarPopupToggle(d)"
            type="button" tabindex="-1"
            [disabled]="isDisabled()"
          >
          </button>
        </div>
      </div>
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>
  `,
  styles: [`

    .custom-datepicker {
      margin: 0;
    }

    .custom-datepicker .input-group {
      width: 85%;
    }

    .custom-datepicker .input-group > .input-group-append > .btn {
      border-radius: 0;
      font-size: 14px !important;
    }

    .custom-datepicker .form-control {
      margin: 0;
      width: 80% !important;
    }

    .custom-datepicker .calendar:focus {
      box-shadow: none;
    }

    .custom-datepicker .spinner-container {
      display: inline-block;
      position: absolute;
      top: -1px;
      right: 28px;
      z-index: 1;
    }

    .custom-datepicker .close-icon {
      position: absolute;
      right: 40px;
      top: 1px;
      font-size: 20px;
      line-height: 24px;
      color: #a3a9ad;
      cursor: pointer;
      z-index: 10;
    }
  `
  ],
  providers: [
    NgbDatepickerConfig, // add NgbDatepickerConfig to the component providers
    {provide: NgbDateParserFormatter, useClass: EvNgbDateParserFormatterService}
  ]
})
export class EvNgDatePickerComponent extends BaseFieldTypeComponent {

  dateModelValue: NgbDate;
  @ViewChild('d', {static: true}) d: any;

  constructor(questionnaireService: QuestionnaireService,
              focusManagerService: FocusManagerService,
              modalService: ModalService,
              private config: NgbDatepickerConfig,
              private calendar: NgbCalendar) {
    super(questionnaireService, focusManagerService, modalService);
  }

  initializeModelValue() {
    this.setPlaceholderInTemplateOptions();
    this.initDatePickerConfiguration();
    this.initDateModelValue();
  }

  initDatePickerConfiguration() {
    // customize default values of date pickers used by this component
    this.config.minDate = this.getConfigDate('minDate', {year: 1900, month: 1, day: 1});
    this.configMaxDate();
    // days that don't belong to current month are not visible
    this.config.outsideDays = 'hidden';
  }


  private getConfigDate(attributeName: string, defaultDate: NgbDateStruct): NgbDateStruct {
    const attributes = this.getTemplateAttributes();
    const dateValueStr: string = attributes[attributeName] as string;
    if (!dateValueStr) {
      return defaultDate;
    }
    const dateValues = dateValueStr.split('/');
    return {year: +dateValues[0], month: +dateValues[1], day: +dateValues[2]};
  }


  initDateModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[fieldKey];
    this.setDateModelValue(modelValue);

    if (this.field['opened']) {
      this.d.open();
      this.d.navigateTo(this.dateModelValue);
      this.focusManagerService.setActiveFieldPath(null);
    }
  }

  onCalendarPopupToggle(d) {
    d.toggle();
    d._elRef.nativeElement.focus();
  }

  onDateSelectHandler(d) {
    const templateOptions = this.field.templateOptions;
    templateOptions.focus = null;
    d.close();
  }

  clearDate() {
    this.dateModelValue = null;
    const dateValue = '';
    this.saveAnswer(dateValue);
  }

  onKeyDownHandler(e) {
    // allow only enter, tab, shift keys
    if (e.keyCode === 13 || e.keyCode === 16 || e.keyCode === 9) {
      return true;
    }
    return false;
  }

  onPasteHandler(e) {
    e.preventDefault();
  }

  onCloseHandler() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[fieldKey];
    if (!this.dateModelValue) {
      return;
    }
    const dateValue = this.dateModelValue.year + '-' + this.dateModelValue.month + '-' + this.dateModelValue.day;
    if (!this.isEqualDateValue(new Date(modelValue), new Date(dateValue))) {
      this.saveAnswer(dateValue);
      this.focusManagerService.setActiveFieldPath(null);
    }
  }

  configMaxDate() {
    const attributes = this.getTemplateAttributes();
    if (attributes.maxDate && attributes.maxDate === NO_MAX_DATE) {
      return;
    } // Max date configuration for NoMaxDate from data
    this.config.maxDate = this.getConfigDate('maxDate', this.calendar.getToday());
  }

  isEqualDateValue(modelValue, value) {
    return (modelValue.getFullYear() === value.getFullYear()) &&
      (modelValue.getMonth() === value.getMonth()) &&
      (modelValue.getDate() === value.getDate());
  }


  setPlaceholderInTemplateOptions() {
    const templateOptions = this.field.templateOptions;
    templateOptions.placeholder = 'Click calendar to select date';
  }

  resetModelValue(resetValue) {
    this.setDateModelValue(resetValue);
  }

  private setDateModelValue(modelValue: string) {
    if (modelValue) {
      const splittedModelValue = modelValue.split('/');
      const yearValue = +splittedModelValue[0];
      const monthValue = +splittedModelValue[1];
      const dayValue = +splittedModelValue[2];
      this.dateModelValue = new NgbDate(yearValue, monthValue, dayValue);
    }
  }
}
