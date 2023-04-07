import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

import { getOnlyDate } from './get-only-date';

export function getFullDate(date: Date) {
  const dateAdapt: NgbDateStruct = getOnlyDate(date);
  return new Date(dateAdapt.year, dateAdapt.month - 1, dateAdapt.day);
}
