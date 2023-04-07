import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { select, Store } from '@ngrx/store';
import { CookieService } from 'ngx-cookie-service';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { RequestState } from '../ngrx/utils';
import { State } from '../ngrx/state';
import { Login, LoginInModal, Logout, PreLoginInModal } from '../ngrx/user/user.actions';
import { getCurrentUserToken, getLoggedIn } from '../ngrx/user/user.state';
import { selectUserGetState } from '../ngrx/user-requests/state';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import {
  selectChangePasswordPutRequestState,
  selectLoginInModalRequestState,
  selectLoginRequestState,
  selectLogoutRequestState
} from '../ngrx/auth-requests/state';

import { LoginResponse } from '../models/login-response.model';
import { PutPassword, ResetRedirectUrl, SetRedirectUrl } from '../ngrx/auth/auth.actions';

import { authTokenKey } from './user.service';

@Injectable()
export class AuthService {

  loginRequest$: Observable<RequestState<LoginResponse>>;
  loginInModalRequest$: Observable<RequestState<LoginResponse>>;
  logoutRequest$: Observable<RequestState<any>>;
  getUserRequest$: Observable<RequestState<any>>;
  changePasswordPutRequest$: Observable<RequestState<{access_token: string}>>;
  isLoggedIn$: Observable<boolean>;
  currentUserToken$: Observable<string>;

  constructor(
    private httpClient: HttpClient,
    private store: Store<State>,
    private cookieService: CookieService,
  ) {
    this.loginRequest$ = this.store.pipe(select(selectLoginRequestState));
    this.loginInModalRequest$ = this.store.pipe(select(selectLoginInModalRequestState));
    this.logoutRequest$ = this.store.pipe(select(selectLogoutRequestState));
    this.changePasswordPutRequest$ = this.store.pipe(select(selectChangePasswordPutRequestState));
    this.getUserRequest$ = this.store.pipe(select(selectUserGetState));
    this.isLoggedIn$ = this.store.pipe(select(getLoggedIn));
    this.currentUserToken$ = this.store.pipe(select(getCurrentUserToken));
  }

  login(username, password) {
    this.store.dispatch(new Login({ username, password }));
    return this.loginRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  loginInModal(username, password) {
    this.store.dispatch(new LoginInModal({ username, password }));
    return this.loginInModalRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  preLoginInModal() {
    this.store.dispatch(new PreLoginInModal());
  }

  logout() {
    this.store.dispatch(new Logout());
    return this.logoutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  changePassword(data) {
    this.store.dispatch(new PutPassword(data));
    return this.changePasswordPutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  setRedirectUrl(url) {
    this.store.dispatch(new SetRedirectUrl(url));
  }

  resetRedirectUrl() {
    this.store.dispatch(new ResetRedirectUrl());
  }

  getAuthToken() {
    return this.cookieService.get(authTokenKey);
  }

  setAuthToken(token) {
    this.cookieService.set(authTokenKey, token, 1, '/', null, null, 'Strict');
  }

  removeAuthToken() {
    // TODO: used to remove the cookie. remove when update to new version of ngx-cookie-service
    this.cookieService.set(authTokenKey, '', -1, '/', null, null, 'Strict');
  }

  removeAllCookies() {
    this.cookieService.deleteAll('/');
  }
}
