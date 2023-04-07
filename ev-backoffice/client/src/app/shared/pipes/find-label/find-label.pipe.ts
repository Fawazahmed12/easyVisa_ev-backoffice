import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'findLabel'})
export class FindLabelPipe implements PipeTransform {
  transform(value: any, array: any[], fullLabel?: boolean, searchLabel?: boolean) {

    const foundedItem = array.find((item) => item.value === value);
    if (fullLabel) {
      return foundedItem && foundedItem.fullLabel || null;
    } else if (searchLabel) {
      return foundedItem && foundedItem.searchLabel || null;
    }
    return foundedItem && foundedItem.label || null;
  }
}
