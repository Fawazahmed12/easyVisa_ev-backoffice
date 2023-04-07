import { Injectable } from '@angular/core';

import { from, Observable, ReplaySubject, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

import { environment } from '../../../environments/environment';

export class Address {
  address_1: string;
  address_2?: string;
  address_city: string;
  address_state: string;
  address_zip: string;
  address_country: string;
}

export class ExtraDetails extends Address {
  firstname: string;
  lastname: string;
  month: string;
  year: string;
  phone: string;
  url: string;
  validate?: boolean;
  customer_id?: string;
}

export class CustomerData extends Address {
  allow_invoice_credit_card_payments: boolean;
  cc_emails?: string;
  cc_sms?: string;
  company: string;
  created_at: string;
  deleted_at?: string;
  email?: string;
  firstname: string;
  gravatar: boolean;
  id: string;
  lastname: string;
  notes?: string;
  options?: string;
  phone: string;
  reference?: string;
  updated_at: string;
}

export interface TokenizeData extends Address {
  bank_holder_type?: string;
  bank_name?: string;
  bank_type?: string;
  card_exp: string;
  card_exp_datetime: string;
  card_last_four: string;
  card_type: string;
  created_at: string;
  customer: CustomerData;
  customer_id: string;
  has_cvv: boolean;
  id: string;
  is_default: number;
  is_usable_in_vt: boolean;
  method: string;
  nickname: string;
  person_name: string;
  updated_at: string;
}

export interface CardProperties {
  id: string;
  placeholder?: string;
  style?: string;
}

export interface TokenizeDataShrink {
  address_1: string;
  address_2?: string;
  address_city: string;
  address_country: string;
  address_state: string;
  address_zip: string;
  card_exp: string;
  person_name: string;
  card_last_four: string;
  card_type: string;
  id: string;
  customer_id: string;
}

@Injectable()
export class FattService {
  fattJs: FattJs;
  fattLoadingStatusSubject$: ReplaySubject<boolean> = new ReplaySubject(1);
  fattIsValidSubject$: ReplaySubject<boolean> = new ReplaySubject(1);

  showCardForm(dataCard: { number: CardProperties; cvv: CardProperties }) {
    this.fattJs = new FattJs(environment.fattKey, {
      number: {
        id: dataCard.number.id,
        placeholder: dataCard.number.placeholder,
        style: dataCard.number.style,
      },
      cvv: {
        id: dataCard.cvv.id,
        placeholder: dataCard.cvv.placeholder,
        style: dataCard.cvv.style,
      },
    });
    this.fattJs.showCardForm()
    .catch(err => {
      console.log('there was an error loading the form: ', err);
    });
  }

  tokenizeMethod(extraDetails: ExtraDetails): Observable<TokenizeData> {
    this.fattLoadingStatusSubject$.next(true);
    return from(this.fattJs.tokenize(extraDetails)).pipe(
      tap(() => {
        this.fattLoadingStatusSubject$.next(false);
      }),
      catchError((err) => {
        this.fattLoadingStatusSubject$.next(false);
        return throwError(err);
      })
    );
  }

  getIsValidFattForm() {
    this.fattJs.on('card_form_complete', () => {
      this.fattIsValidSubject$.next(true);
    });
  }

  getIsUnValidFattForm() {
    this.fattJs.on('card_form_incomplete', () => {
      this.fattIsValidSubject$.next(false);
    });
  }
}
