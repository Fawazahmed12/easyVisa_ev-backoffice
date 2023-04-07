import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { RequestState } from '../../../core/ngrx/utils';
import { State } from '../../../core/ngrx/state';
import { throwIfRequestFailError } from '../../../core/ngrx/utils/rxjs-utils';

import { getInvitePutRequestState } from '../../ngrx/state';
import { inviteAttorneyPutRequestHandler } from '../../ngrx/requests/invite-attorney-put/state';


@Injectable()
export class CreateLegalPracticeService {

  inviteAttorneyPutState$: Observable<RequestState<any>>;

  constructor(
    private store: Store<State>
  ) {
    this.inviteAttorneyPutState$ = this.store.pipe(select(getInvitePutRequestState));
  }

  inviteAttorney(memberDetails) {
    this.store.dispatch(inviteAttorneyPutRequestHandler.requestAction(memberDetails));
    return this.inviteAttorneyPutState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }
}
