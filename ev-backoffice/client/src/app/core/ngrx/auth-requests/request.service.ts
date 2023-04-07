import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { LoginResponse } from '../../models/login-response.model';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Injectable()
export class AuthRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  loginRequest(data) {
     return this.httpClient.post<LoginResponse>(
       `/login`,
       data
     );
  }

  logoutRequest() {
    return this.httpClient.post(`logout`, null).pipe(
      map(() => ({}))
    );
  }

  changePasswordPutRequest(data): Observable<{access_token: string}> {
    return this.httpClient.put<{access_token: string}>(`users/change-password`, data);
  }
}
