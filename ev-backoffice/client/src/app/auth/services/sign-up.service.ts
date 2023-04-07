import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';
import { State } from '../../core/ngrx/state';
import { Attorney } from '../../core/models/attorney.model';
import { Profile } from '../../core/models/profile.model';
import { LoginResponse } from '../../core/models/login-response.model';
import { SetRegistrationRepresentativeType } from '../../core/ngrx/user/user.actions';

import {
  getAddReferralPostRequestState,
  getAttorneyPostState,
  getAttorneySignUpInfo,
  getCompletePaymentPostRequestState,
  getPaymentPostRequestState, getReferralEmail,
  getRepresentativeTypePostRequestState,
  getSignUpInfoRequestState,
  getUserPostRequestState,
  getVerifyAttorneyPostRequestState
} from '../ngrx/state';
import { PostAddReferral, PostAttorneyUser } from '../ngrx/attorney-sign-up-info/attorney-sign-up-info.actions';
import { verifyAttorneyPostRequestHandler } from '../ngrx/requests/verify-attorney-post/state';
import { representativeTypePostRequestHandler } from '../ngrx/requests/representative-type-post/state';
import { paymentPostRequestHandler } from '../ngrx/requests/payment-post/state';
import { completePaymentPostRequestHandler } from '../ngrx/requests/complete-payment-post/state';
import { signUpInfoGetRequestHandler, userPostRequestHandler } from '../ngrx/requests/state';

@Injectable()
export class SignUpService {
  signUpInfoGetRequest$: Observable<RequestState<Profile>>;
  attorneyPostState$: Observable<RequestState<Attorney>>;
  attorneySignUpInfo$: Observable<Attorney>;
  verifyAttorneyRequest$: Observable<RequestState<string>>;
  updateRepresentativeTypeRequest$: Observable<RequestState<{ message: string }>>;
  paymentRequest$: Observable<RequestState<{ token: string }>>;
  completePaymentRequest$: Observable<RequestState<Attorney>>;
  userPostRequest$: Observable<RequestState<LoginResponse>>;
  addReferralPostRequest$: Observable<RequestState<{email: string}>>;
  referralEmail$: Observable<string>;

  constructor(
    private httpClient: HttpClient,
    private store: Store<State>,
  ) {
    this.attorneyPostState$ = this.store.pipe(select(getAttorneyPostState));
    this.attorneySignUpInfo$ = this.store.pipe(select(getAttorneySignUpInfo));
    this.signUpInfoGetRequest$ = this.store.pipe(select(getSignUpInfoRequestState));
    this.verifyAttorneyRequest$ = this.store.pipe(select(getVerifyAttorneyPostRequestState));
    this.updateRepresentativeTypeRequest$ = this.store.pipe(select(getRepresentativeTypePostRequestState));
    this.paymentRequest$ = this.store.pipe(select(getPaymentPostRequestState));
    this.completePaymentRequest$ = this.store.pipe(select(getCompletePaymentPostRequestState));
    this.userPostRequest$ = this.store.pipe(select(getUserPostRequestState));
    this.addReferralPostRequest$ = this.store.pipe(select(getAddReferralPostRequestState));
    this.referralEmail$ = this.store.pipe(select(getReferralEmail));
  }

  usernameValidateRequest(username) {
    return this.httpClient.post(`/public/validate-username`, {username});
  }

  emailValidateRequest(email) {
    return this.httpClient.post(`/public/validate-email`, {email});
  }

  createAttorney(data) {
    this.store.dispatch(new PostAttorneyUser(data));
    return this.attorneyPostState$.pipe(
      filter((state) => !state.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  setRegistrationRepresentativeType(representativeType: string) {
    this.store.dispatch(new SetRegistrationRepresentativeType(representativeType));
  }

  updateRepresentativeType(data) {
    this.store.dispatch(representativeTypePostRequestHandler.requestAction(data));
    return this.updateRepresentativeTypeRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  verifyAttorney(token) {
    this.store.dispatch(verifyAttorneyPostRequestHandler.requestAction(token));
    return this.verifyAttorneyRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  sendPayment(data) {
    this.store.dispatch(paymentPostRequestHandler.requestAction(data));
    return this.paymentRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  completePayment(data) {
    this.store.dispatch(completePaymentPostRequestHandler.requestAction(data));
    return this.completePaymentRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  getSignUpData(token) {
    this.store.dispatch(signUpInfoGetRequestHandler.requestAction(token));
    return this.signUpInfoGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  createUser(data) {
    this.store.dispatch(userPostRequestHandler.requestAction(data));
    return this.userPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  addReferral(data) {
    this.store.dispatch(new PostAddReferral(data));
  }
}
