import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'numberOfYears'})
export class NumberOfYearsPipe implements PipeTransform {
  transform(date: any) {
    const startDate = new Date(date).getTime();
    const toDay = new Date().getTime();
    const numberOfYears = (toDay - startDate) / (365 * 24 * 60 * 60 * 1000);
    const result = Math.floor(numberOfYears);
    return date ? result < 1 ? '<1' : result : null;
  }
}
