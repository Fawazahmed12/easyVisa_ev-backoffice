import { Observable, ReplaySubject } from 'rxjs';

import { Injectable } from '@angular/core';

@Injectable()
export class PaymentServiceMock {

  paymentMethodPutRequestSubject$ = new ReplaySubject<{loading: boolean}>(1);

  get paymentMethodPutRequest$(): Observable<{loading: boolean}> {
    this.paymentMethodPutRequestSubject$.next({loading: true});
    return this.paymentMethodPutRequestSubject$.asObservable();
  }
}
