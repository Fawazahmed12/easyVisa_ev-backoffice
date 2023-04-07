import { Pipe, PipeTransform } from '@angular/core';


@Pipe({name: 'numberHalfRound'})
export class NumberHalfRoundPipe implements PipeTransform {
  transform(value: number) {
    return (Math.ceil(value * 2) / 2);
  }
}
