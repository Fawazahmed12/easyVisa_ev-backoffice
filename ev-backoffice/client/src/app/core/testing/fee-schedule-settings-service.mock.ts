import { Injectable } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { FeeSchedule } from '../models/fee-schedule.model';
import { BenefitCategories } from '../models/benefit-categories.enum';

@Injectable()
export class FeeScheduleSettingsServiceMock {

  feeScheduleSettings$ = new ReplaySubject<FeeSchedule[]>(1);

  get feeDetails$() {
    this.feeScheduleSettings$.next([
      {
        amount: 135,
        benefitCategory: BenefitCategories.IR1,
        id: 348053,
        representativeId: 138
      },
      {
        amount: 235,
        benefitCategory: BenefitCategories.K1K3,
        id: 348054,
        representativeId: 245
      }
    ]);
    return this.feeScheduleSettings$.asObservable();
  }
}
