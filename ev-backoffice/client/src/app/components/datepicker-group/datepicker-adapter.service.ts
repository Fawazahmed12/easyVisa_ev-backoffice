import { Injectable } from '@angular/core';
import { NgbDateAdapter, NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

import { getAmericanFormatDate } from '../../shared/utils/get-american-format-date';

@Injectable()
export class NgbDateISOAdapter extends NgbDateAdapter<Date> {

  fromModel(date): any {
    if (date) {
      const utcDate = new Date(date.replace(/-/g, ','));
      return {
        year: utcDate.getFullYear(),
        month: utcDate.getMonth() + 1,
        day: utcDate.getDate()
      };
    } else {
      return null;
    }
  }

  toModel(date: NgbDateStruct): any {
    return date ? getAmericanFormatDate(date) : null;
  }
}
