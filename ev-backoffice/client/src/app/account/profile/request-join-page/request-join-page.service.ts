import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { RequestState } from '../../../core/ngrx/utils';
import { State } from '../../../core/ngrx/state';

import { getIsAdminPostRequestState, getRequestJoinPutRequestState } from '../../ngrx/state';
import { requestJoinPutRequestHandler } from '../../ngrx/requests/request-join-put/state';
import { isAdminPostRequestHandler } from '../../ngrx/requests/is-admin-post/state';
import { filter, share } from 'rxjs/operators';
import { throwIfRequestFailError } from '../../../core/ngrx/utils/rxjs-utils';


@Injectable()
export class RequestJoinPageService {

  requestJoinPutState$: Observable<RequestState<any>>;
  isAdminPostState$: Observable<RequestState<any>>;

  constructor(
    private store: Store<State>
  ) {
    this.requestJoinPutState$ = this.store.pipe(select(getRequestJoinPutRequestState));
    this.isAdminPostState$ = this.store.pipe(select(getIsAdminPostRequestState));
  }

  requestJoin(data) {
    this.store.dispatch(requestJoinPutRequestHandler.requestAction(data));
    return this.requestJoinPutState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  isAdmin(data) {
    this.store.dispatch(isAdminPostRequestHandler.requestAction(data));
    return this.isAdminPostState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }
}
