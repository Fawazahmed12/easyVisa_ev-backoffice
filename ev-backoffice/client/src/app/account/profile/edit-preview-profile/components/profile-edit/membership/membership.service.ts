import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';

import { State } from '../../../../../../core/ngrx/state';

import { RequestState } from '../../../../../../core/ngrx/utils';
import { leaveOrganizationPostRequestHandler } from '../../../../../ngrx/requests/leave-organization-post/state';
import { getLeaveOrganizationPostRequestState } from '../../../../../ngrx/state';
import { filter, share } from 'rxjs/operators';
import { throwIfRequestFailError } from '../../../../../../core/ngrx/utils/rxjs-utils';


@Injectable()
export class MembershipService {

  leaveOrganizationPostState$: Observable<RequestState<any>>;

  constructor(
    private store: Store<State>
  ) {
    this.leaveOrganizationPostState$ = this.store.pipe(select(getLeaveOrganizationPostRequestState));
  }

  leaveOrganization(data) {
    this.store.dispatch(leaveOrganizationPostRequestHandler.requestAction(data));
    return this.leaveOrganizationPostState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }
}
