export function getOnlyDate(date: Date) {
  return {year: date.getFullYear(), month: date.getMonth() + 1, day: date.getDate()};
}
