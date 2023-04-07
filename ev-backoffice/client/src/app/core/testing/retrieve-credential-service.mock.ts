import { Injectable } from '@angular/core';

import { Observable, of, ReplaySubject } from 'rxjs';

import { ResponseStatus } from '../ngrx/utils';

@Injectable()
export class RetrieveCredentialServiceMock {
  activePackageIdSubject$ = new ReplaySubject<number>(1);

  forgotPassword(): Observable<any> {
    return of({
      loading: false,
      loaded: true,
      status: ResponseStatus.success,
      data: 'test data',
    });
  }

  forgotUsername(): Observable<any> {
    return of({
      loading: false,
      loaded: true,
      status: ResponseStatus.success,
      data: 'test data',
    });
  }
}
