import { DayOfWeek } from './day-of-week.enum';

export interface WorkingTime {
  hour: number;
  minute: number;
}

export class WorkingHours {
  dayOfWeek: DayOfWeek;
  start: WorkingTime;
  end: WorkingTime;
}
