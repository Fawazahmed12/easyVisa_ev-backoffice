import { Injectable } from '@angular/core';

import { Observable, ReplaySubject } from 'rxjs';

import { EstimatedTax } from '../models/estimated-tax.model';

@Injectable()
export class TaxesServiceMock {

  postFeeWithEstimatedTaxRequestStateSubject$ = new ReplaySubject<{ loading: boolean }>(1);
  feeSubject$ = new ReplaySubject<EstimatedTax>(1);

  feeMock = {
    subTotal: 100,
    estTax: 100,
    grandTotal: 100,
    credit: 100,
  };

  get postFeeWithEstimatedTaxRequestState$(): Observable<{ loading: boolean }> {
    this.postFeeWithEstimatedTaxRequestStateSubject$.next({loading: true});
    return this.postFeeWithEstimatedTaxRequestStateSubject$.asObservable();
  }

  get fee$(): Observable<EstimatedTax> {
    this.feeSubject$.next(this.feeMock);
    return this.feeSubject$.asObservable();
  }
}
