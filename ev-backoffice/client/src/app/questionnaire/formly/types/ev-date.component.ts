import { Component } from '@angular/core';

import { isEmpty } from 'lodash-es';

import { BaseFieldTypeComponent } from './base-fieldtype.component';


@Component({
  selector: 'app-formly-custom-date',
  template: `
    <div class="row custom-date" [formlyAttributes]="field">
      <div class="col-5 pr-0 pl-0">
        <select
          [value]="selectedMonth"
          id="month" name="month" class="form-control"
          (change)="onCustomMonthChange($event)"
          [disabled]="isDisabled()"
        >
          <option value="" [selected]="!selectedMonth ? true : null" [hidden]="selectedMonth">-Month-</option>
          <option value="01">January</option>
          <option value="02">February</option>
          <option value="03">March</option>
          <option value="04">April</option>
          <option value="05">May</option>
          <option value="06">June</option>
          <option value="07">July</option>
          <option value="08">August</option>
          <option value="09">September</option>
          <option value="10">October</option>
          <option value="11">November</option>
          <option value="12">December</option>
        </select>
      </div>
      <div class="col-3 pr-0 padding-left-5">
        <select
          [value]="selectedDay"
          id="day" name="day" class="form-control" (change)="onCustomDayChange($event)"
          [disabled]="isDisabled()"
        >
          <option value="" [selected]="!selectedDay ? true : null" [hidden]="selectedDay">-Day-</option>
          <option *ngFor="let day of getDays()" [value]="day">{{day}}</option>
        </select>
      </div>
      <div class="col-4 pr-0 padding-left-5">
        <select
          [value]="selectedYear"
          id="year" name="year" class="form-control" (change)="onCustomYearChange($event)"
          [disabled]="isDisabled()"
        >
          <option value="" [selected]="!selectedYear ? true : null" [hidden]="selectedYear">-Year-</option>
          <option *ngFor="let year of getYears()" [value]="year">{{year}}</option>
        </select>
      </div>
      <span class="spinner-container" *ngIf="isLoading$ | async">
        <img src="../../../../assets/images/spinner.gif"/>
      </span>
    </div>
  `,
  styles: [
    `
      .custom-date {
        margin: 0;
        position: relative;
        text-align: left;
        width: 85%;
      }

      .custom-date .form-control {
        width: 100% !important;
      }

      .custom-date .padding-left-5 {
        padding-left: 5px;
      }

      .custom-date .spinner-container {
        display: inline-block;
        position: absolute;
        top: -1px;
        right: -40px;
        z-index: 1;
      }
    `
  ]
})
export class EvDateComponent extends BaseFieldTypeComponent {

  EASY_VISA_DATE_FORMAT = 'yyyy/MM/dd'; // This is the internal format

  selectedDay: number;
  selectedMonth: number;
  selectedYear: number;

  initializeModelValue() {
    this.initDateModelValue();
  }

  initDateModelValue() {
    const fieldKey = this.field.key as string;
    const modelValue = this.field.model[ fieldKey ];
    if (modelValue) {
      const splittedModelValue = modelValue.split('/');
      this.selectedYear = splittedModelValue[ 0 ];
      this.selectedMonth = splittedModelValue[ 1 ];
      this.selectedDay = splittedModelValue[ 2 ];
    }
  }

  getYears() {
    const startYear = new Date(0).getFullYear();
    const endYear = new Date().getFullYear();
    const dateFormats = this.EASY_VISA_DATE_FORMAT.split('/');
    const years = this.constructRangeValues(startYear, endYear, dateFormats[ 0 ].length);
    return years;
  }


  getDays() {
    const today = new Date();
    const yearValue = this.selectedYear || today.getFullYear();
    // As js-date object treats month as zero Index value, here need to subtract one..
    const monthValue = this.selectedMonth ? (this.selectedMonth - 1) : today.getMonth();
    const selectedDate = new Date(yearValue, monthValue, 1);
    const lastDayOfMonth = new Date(selectedDate.getFullYear(), selectedDate.getMonth() + 1, 0);
    const dateFormats = this.EASY_VISA_DATE_FORMAT.split('/');
    const days = this.constructRangeValues(1, lastDayOfMonth.getDate(), dateFormats[ 2 ].length);
    return days;
  }

  onCustomDayChange(e) {
    this.selectedDay = e.target.value;
    this.saveDateAnswer();
  }

  onCustomMonthChange(e) {
    this.selectedMonth = e.target.value;
    this.validateDateValue();
    this.saveDateAnswer();
  }

  onCustomYearChange(e) {
    this.selectedYear = e.target.value;
    this.validateDateValue();
    this.saveDateAnswer();
  }

  constructRangeValues(start, end, numberOfChars) {
    const output = [];
    for (let i = start; i <= end; i++) {
      output.push(this.roundOff(i, numberOfChars));
    }
    return output;
  }

  roundOff(inputValue, numberOfChars) {
    let roundedVal: string = inputValue.toString();
    while (roundedVal.length < numberOfChars) {
      roundedVal = '0' + roundedVal;
    }
    return roundedVal;
  }


  validateDateValue() {
    if (this.selectedDay && this.selectedMonth && this.selectedYear) {
      // As js-date object treats month as zero Index value, here need to subtract one..
      const monthValue = this.selectedMonth - 1;
      const selectedDate = new Date(this.selectedYear, monthValue, this.selectedDay);
      if (isNaN(selectedDate.getTime())) {
        this.selectedDay = null;
      }
    }
  }

  saveDateAnswer() {
    if (this.selectedDay && this.selectedMonth && this.selectedYear) {
      const dateValue = [ this.selectedYear, this.selectedMonth, this.selectedDay ].join('-');
      this.saveAnswer(dateValue);
    }
  }
}
