import { FeeSchedule } from './fee-schedule.model';

export interface EstimatedTax {
  subTotal: number;
  estTax: number;
  grandTotal: number;
  credit?: number;
  immediateCharge?: FeeSchedule[];
  laterCharge?: FeeSchedule[];
}
