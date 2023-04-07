import { Injectable } from '@angular/core';

import { NgbDateParserFormatter, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

function isNumber(value: any): boolean {
  return !isNaN(toInteger(value));
}

function toInteger(value: any): number {
  return parseInt(`${value}`, 10);
}

function getDate(datePart: string): number | null {
  return datePart && isNumber(datePart) ? toInteger(datePart) : null;
}

function getCommonFormatDate(date) {
  const utcDate: Date = new Date(Date.UTC(date.year, date.month - 1, date.day, 0, 0, 0, 0o0));
  utcDate.setMinutes( utcDate.getMinutes() + utcDate.getTimezoneOffset() );
  const onlyDate = getDateWithLongMonth(utcDate);
  return `${onlyDate.month} ${onlyDate.day}, ${onlyDate.year}`;
}

function getDateWithLongMonth(date: Date) {
  const options = { month: 'long'} as const;
  const monthName = new Intl.DateTimeFormat('en-US', options).format(date);
  return {year: date.getFullYear(), month: monthName, day: date.getDate()};
}

@Injectable()
export class EvNgbDateParserFormatterService extends NgbDateParserFormatter {

  parse(value: string): NgbDateStruct {
    if (value) {
      const [month, day, year] = value.trim().split('-');
      return  {
        month: getDate(month),
        day: getDate(day),
        year: getDate(year),
      };
    }
    return null;
  }

  format(date: NgbDateStruct): string {
    return date ? getCommonFormatDate(date) : null;
  }

}
