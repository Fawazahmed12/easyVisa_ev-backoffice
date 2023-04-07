import { Observable, of, ReplaySubject } from 'rxjs';

import { RequestState, ResponseStatus } from '../ngrx/utils';
import { Attorney } from '../models/attorney.model';


export class SignUpServiceMock {
  attorneyRequestSubject$ = new ReplaySubject<RequestState<Attorney>>(1);
  attorneySignUpInfoSubject$ = new ReplaySubject<Attorney>(1);
  signUpInfoGetRequestSubject$ = new ReplaySubject<RequestState<any>>(1);
  resCreateAttorneySubject$ = new ReplaySubject<any>(1);
  completePaymentRequestSubject$ = new ReplaySubject<RequestState<Attorney>>(1);



  get attorneyRequest$(): Observable<RequestState<Attorney>> {
    return this.attorneyRequestSubject$.asObservable();
  }

  get attorneySignUpInfo$(): Observable<Attorney> {
    return this.attorneySignUpInfoSubject$.asObservable();
  }

  get signUpInfoGetRequest$() {
    this.signUpInfoGetRequestSubject$.next({
      loading: false,
      loaded: true,
      status: ResponseStatus.success,
      data: 'test data',
    });
    return this.signUpInfoGetRequestSubject$.asObservable();
  }

  get completePaymentRequest$(): Observable<RequestState<Attorney>> {
    return this.completePaymentRequestSubject$.asObservable();
  }

  emailValidateRequest(): Observable<{}> {
    return of({ valid: true });
  }

  verifyAttorney(token): Observable<RequestState<string>> {
    return of({
      loading: false,
      loaded: true,
      status: ResponseStatus.success,
      data: 'test data',
    });
  }

  usernameValidateRequest(username) {
    return of({ valid: true });
  }

  createAttorney(data) {
    this.resCreateAttorneySubject$.next({
      loading: false,
      loaded: true,
      status: ResponseStatus.success,
      data: 'test data',
    });
    return this.resCreateAttorneySubject$.asObservable();
  }

  completePayment(username) {
    return of({});
  }

  updateRepresentativeType(data?) {
    return of({});
  }

  setRegistrationRepresentativeType(data?) {
    return of({});
  }
}
