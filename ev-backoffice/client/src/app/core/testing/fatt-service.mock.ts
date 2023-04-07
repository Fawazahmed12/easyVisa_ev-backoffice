import { Injectable } from '@angular/core';

import { Observable, ReplaySubject } from 'rxjs';

import { TokenizeData } from '../../../typings/fattjs';
import { TokenizeDataShrink } from '../services';

@Injectable()
export class FattServiceMock {
  tokenizeDataSubject$ = new ReplaySubject<TokenizeDataShrink>(1);
  fattLoadingStatusSubjectMock$ = new ReplaySubject<boolean>(1);
  fattIsValidSubjectMock$ = new ReplaySubject<boolean>(1);

  fattJs = {
    validCvv: true,
    validNumber: true
  };

  showCardForm(): void {
  }

  tokenizeMethod(): Observable<TokenizeDataShrink> {
    this.tokenizeDataSubject$.next({
      address_1: '',
      address_2: '',
      address_city: '',
      address_country: '',
      address_state: '',
      address_zip: '',
      card_exp: '',
      person_name: '',
      card_last_four: '',
      card_type: '',
      id: '',
      customer_id: '',
    });
    return this.tokenizeDataSubject$.asObservable();
  }

  get fattLoadingStatusSubject$() {
    return this.fattLoadingStatusSubjectMock$.asObservable();
  }

  get fattIsValidSubject$() {
    return this.fattIsValidSubjectMock$.asObservable();
  }

  getIsUnValidFattForm() {
  }

  getIsValidFattForm() {
  }
}

