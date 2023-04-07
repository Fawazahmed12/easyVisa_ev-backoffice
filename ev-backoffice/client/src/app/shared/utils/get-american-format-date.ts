import { getOnlyDate } from './get-only-date';

export function getAmericanFormatDate(date) {
  const utcDate: Date = new Date(Date.UTC(date.year, date.month - 1, date.day, 0, 0, 0, 0o0));
  utcDate.setMinutes( utcDate.getMinutes() + utcDate.getTimezoneOffset() );
  const onlyDate = getOnlyDate(utcDate);
  const twoDigitsMonth = onlyDate.month < 10 ? '0' + onlyDate.month : onlyDate.month;
  const twoDigitsDay = onlyDate.day < 10 ? '0' + onlyDate.day : onlyDate.day;
  return `${twoDigitsMonth}-${twoDigitsDay}-${onlyDate.year}`;
}
