import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import {
  getFinancialDetails,
  getFinancialDetailsGetRequestState,
  postInviteColleaguesPostRequestState
} from '../ngrx/state';
import { FinancialDetails } from '../models/financial-details.model';
import { GetFinancialDetails, PostInviteColleagues } from '../ngrx/financial/financial.actions';
import { Email } from '../../core/models/email.model';

@Injectable()
export class FinancialService {
  financialDetails$: Observable<FinancialDetails>;
  financialDetailsGetRequest$: Observable<RequestState<FinancialDetails>>;
  inviteColleaguesPostRequest$: Observable<RequestState<Email>>;

  constructor(
    private store: Store<State>,
  ) {
    this.financialDetails$ = this.store.pipe(select(getFinancialDetails));
    this.financialDetailsGetRequest$ = this.store.pipe(select(getFinancialDetailsGetRequestState));
    this.inviteColleaguesPostRequest$ = this.store.pipe(select(postInviteColleaguesPostRequestState));
  }

  getFinancialDetails(data) {
    this.store.dispatch(new GetFinancialDetails(data));
    return this.financialDetailsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  inviteColleagues(email) {
    this.store.dispatch(new PostInviteColleagues(email));
    return this.inviteColleaguesPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }
}
