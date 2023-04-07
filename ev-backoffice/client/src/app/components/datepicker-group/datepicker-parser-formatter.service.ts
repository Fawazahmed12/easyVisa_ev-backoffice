import { Injectable } from '@angular/core';

import { NgbDateParserFormatter, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

import { getAmericanFormatDate } from '../../shared/utils/get-american-format-date';

function isNumber(value: any): boolean {
  return !isNaN(toInteger(value));
}

function toInteger(value: any): number {
  return parseInt(`${value}`, 10);
}

function getDate(datePart: string): number | null {
  return datePart && isNumber(datePart) ? toInteger(datePart) : null;
}

@Injectable()
export class NgbDateParserFormatterService extends NgbDateParserFormatter {

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
    return date ? getAmericanFormatDate(date) : null;
  }
}
