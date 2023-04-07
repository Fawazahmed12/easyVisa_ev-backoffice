import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'transformTime'})
export class TransformTimePipe implements PipeTransform {
  transform(value: any) {
    let transformNewHour: number;
    let transformNewMinutes: number;
    let amPm: string;

    if (value.hour > 12) {
      transformNewHour = value.hour - 12;
      amPm = 'PM';
    } else {
      transformNewHour = value.hour;
      amPm = 'AM';
    }
    transformNewMinutes = (value.minutes < 10) ? `0${value.minutes}` : value.minutes;
    return `${transformNewHour} : ${transformNewMinutes} ${amPm}`;
  }
}

