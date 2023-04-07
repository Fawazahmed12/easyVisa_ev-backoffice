import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import { MarketingDetails } from '../models/marketing-details.model';
import { getMarketingDetails, getMarketingDetailsGetRequestState } from '../ngrx/state';
import { GetMarketingDetails } from '../ngrx/marketing/marketing.actions';

@Injectable()
export class MarketingService {
  marketingDetails$: Observable<MarketingDetails>;
  marketingDetailsGetRequest$: Observable<RequestState<MarketingDetails>>;

  constructor(
    private store: Store<State>,
  ) {
    this.marketingDetails$ = this.store.pipe(select(getMarketingDetails));
    this.marketingDetailsGetRequest$ = this.store.pipe(select(getMarketingDetailsGetRequestState));
  }

  getMarketingDetails(data) {
    this.store.dispatch(new GetMarketingDetails(data));
    return this.marketingDetailsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }
}
