import { Injectable } from '@angular/core';

import { filter } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { select, Store } from '@ngrx/store';

import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import { getResetPasswordPostRequestState, State } from '../ngrx/state';
import { resetPasswordPostRequestHandler } from '../ngrx/requests/reset-password-post/state';

import { ResetPasswordModel } from '../models/reset-password.model';

@Injectable()
export class ResetPasswordService {

  resetPasswordRequest$: Observable<RequestState<ResetPasswordModel>>;

  constructor(
    private store: Store<State>,
  ) {
    this.resetPasswordRequest$ = this.store.pipe(select(getResetPasswordPostRequestState));
  }

  resetPassword(password: ResetPasswordModel) {
    this.store.dispatch(resetPasswordPostRequestHandler.requestAction(password));
    return this.resetPasswordRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
    );
  }
}
