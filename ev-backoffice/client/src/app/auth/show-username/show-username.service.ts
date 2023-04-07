import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';

import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import {
  getShowUsernamePostRequestState,
  State
} from '../ngrx/state';
import { showUsernamePostRequestHandler } from '../ngrx/requests/show-username-post/state';

@Injectable()
export class ShowUsernameService {
  showUsernameRequest$: Observable<RequestState<{ token: string }>>;

  constructor(
    private store: Store<State>,
  ) {
    this.showUsernameRequest$ = this.store.pipe(select(getShowUsernamePostRequestState));
  }

  getForgottenUsername(token) {
    this.store.dispatch(showUsernamePostRequestHandler.requestAction(token));
    return this.showUsernameRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
    );
  }
}
