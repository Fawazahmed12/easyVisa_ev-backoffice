import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { of } from 'rxjs';
import { delay } from 'rxjs/operators';

import { ResetPasswordModel } from '../../models/reset-password.model';

@Injectable()
export class AuthModuleRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  referringUserRequest(token) {
    // TODO: change when backend would be ready
    return of({
      name: 'testUser',
    });
    // return throwError( {
    //   status: 404,
    //   message: 'Error',
    // });
    // return this.httpClient.get<ReferringUserModel>(
    //   `${environment.apiEndpoint}referringUser`,
    //   token
    // )
  }

  attorneyPostRequest(data) {
    return this.httpClient.post(`/public/attorneys`, data);
  }

  addReferralPostRequest(data) {
    return this.httpClient.post(`attorneys/referral`, {email: data});
  }

  signUpInfoGetRequest(token) {
     return this.httpClient.post(`/public/validate-token`, {token});
  }

  userPostRequest(data) {
     return this.httpClient.post(`/public/register-user`, data);
  }

  representativeTypePostRequest(data) {
    return this.httpClient.post(`/attorneys/${data.id}/update-representative-type`, data);
  }

  forgotUsernamePostRequest(email) {
    return this.httpClient.post(
      `/public/forgot-username`,
      email
    );
  }

  forgotPasswordPostRequest(email) {
    return this.httpClient.post(
      `/public/forgot-password`,
      email
    );
  }

  resetPasswordPostRequest(password) {
    return this.httpClient.post<ResetPasswordModel>(
      `/public/reset-password`,
      password
    );
  }

  verifyAttorneyPostRequest(token) {
    return this.httpClient.post<ResetPasswordModel>(
      `/public/verify-registration`,
      {token}
    );
  }

  showUsernamePostRequest(token) {
    return this.httpClient.post<ResetPasswordModel>(
      `/public/show-username`,
      {token}
    );
  }

  paymentPostRequest(data) {
    // TODO: change when backend would be ready
    return of({
      token: 'paymentCompletedToken'
    }).pipe(
      delay(1000),
    );
  }

  completePaymentPostRequest(data) {
    return this.httpClient.post<ResetPasswordModel>(
      `/attorneys/complete-payment`,
      {...data}
    );
  }
}
