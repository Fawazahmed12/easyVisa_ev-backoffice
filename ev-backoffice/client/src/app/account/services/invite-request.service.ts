import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { select, Store } from '@ngrx/store';
import { filter, share } from 'rxjs/operators';

import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import { Invite } from '../models/invite.model';
import {
  getInvite,
  getInviteDeleteRequestState,
  getInvitePutRequestState,
  getRequestJoin,
  getRequestJoinDeleteRequestState,
  getRequestPutRequestState
} from '../ngrx/state';
import { DeleteInvite, DeleteRequest, PutInvite, PutRequest } from '../ngrx/invite-request/invite-request.action';
import { RequestJoin } from '../models/request-join.model';


@Injectable()
export class InviteRequestService {
  invite$: Observable<Invite>;
  requestJoin$: Observable<RequestJoin>;
  invitePutState$: Observable<RequestState<Invite>>;
  requestPutState$: Observable<RequestState<Invite>>;
  inviteDeleteState$: Observable<RequestState<any>>;
  requestDeleteState$: Observable<RequestState<any>>;

  constructor(
    private store: Store<State>
  ) {
    this.invite$ = this.store.pipe(select(getInvite));
    this.requestJoin$ = this.store.pipe(select(getRequestJoin));
    this.invitePutState$ = this.store.pipe(select(getInvitePutRequestState));
    this.requestPutState$ = this.store.pipe(select(getRequestPutRequestState));
    this.inviteDeleteState$ = this.store.pipe(select(getInviteDeleteRequestState));
    this.requestDeleteState$ = this.store.pipe(select(getRequestJoinDeleteRequestState));
  }

  putInvite(data) {
    this.store.dispatch(new PutInvite(data));
    return this.invitePutState$.pipe(filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  putRequest(data) {
    this.store.dispatch(new PutRequest(data));
    return this.requestPutState$.pipe(filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  deleteInvite() {
    this.store.dispatch(new DeleteInvite());
    return this.inviteDeleteState$.pipe(filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  deleteRequest() {
    this.store.dispatch(new DeleteRequest());
    return this.requestDeleteState$.pipe(filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }
}
