import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';

import { State } from '../ngrx/state';
import { RequestState } from '../ngrx/utils';
import { alertReplyPutRequestHandler, selectAlertReplyRequestState } from '../ngrx/alert-handling-requests/state';
import { filter, share } from 'rxjs/operators';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';


@Injectable()
export class AlertHandlingService {
  getAlertReplyPutRequest$: Observable<RequestState<{ message: string }>>;


  constructor(
    private httpClient: HttpClient,
    private store: Store<State>,
  ) {
    this.getAlertReplyPutRequest$ = this.store.pipe(select(selectAlertReplyRequestState));
  }

  alertReply(data: { id: number; accept: boolean }) {
    this.store.dispatch(alertReplyPutRequestHandler.requestAction(data));
    return this.getAlertReplyPutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }
}
