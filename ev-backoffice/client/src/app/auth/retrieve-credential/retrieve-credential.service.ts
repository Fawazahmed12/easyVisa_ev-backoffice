import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { filter } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { RequestState } from '../../core/ngrx/utils';

import {
  getForgotPasswordPostRequestState,
  getForgotUsernamePostRequestState,
  State
} from '../ngrx/state';
import { forgotUsernamePostRequestHandler } from '../ngrx/requests/forgot-username-post/state';
import { forgotPasswordPostRequestHandler } from '../ngrx/requests/forgot-password-post/state';

@Injectable()
export class RetrieveCredentialService {

  forgotUsernameRequest$: Observable<RequestState<string>>;
  forgotPasswordRequest$: Observable<RequestState<string>>;

  constructor(
    private store: Store<State>,
  ) {
    this.forgotUsernameRequest$ = this.store.pipe(select(getForgotUsernamePostRequestState));
    this.forgotPasswordRequest$ = this.store.pipe(select(getForgotPasswordPostRequestState));
  }

  forgotUsername(email) {
    this.store.dispatch(forgotUsernamePostRequestHandler.requestAction(email));
    return this.forgotUsernameRequest$.pipe(
      filter(response => !response.loading),
    );
  }

  forgotPassword(email) {
    this.store.dispatch(forgotPasswordPostRequestHandler.requestAction(email));
    return this.forgotPasswordRequest$.pipe(
      filter(response => !response.loading),
    );
  }
}
