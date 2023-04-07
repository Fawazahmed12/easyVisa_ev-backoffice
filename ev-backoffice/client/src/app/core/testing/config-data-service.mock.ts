import { Injectable } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { FeeDetails } from '../models/fee-details.model';

@Injectable()
export class ConfigDataServiceMock {

  feeDetailsSubject$ = new ReplaySubject<FeeDetails>(1);

  get feeDetails$() {
    this.feeDetailsSubject$.next({
      signupFee: 999,
      maintenanceFee: 999,
      cloudStorageFee: 999,
      membershipReactivationFee: 999,
      referralBonus: 999,
      signupDiscount: 999,
      articleBonus: 999,
      contactPhone: '999-9999-999',
      supportEmail: 'support@mail.com',
    });
    return this.feeDetailsSubject$.asObservable();
  }
}
