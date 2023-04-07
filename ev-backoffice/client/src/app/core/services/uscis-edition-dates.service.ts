import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { select, Store } from '@ngrx/store';
import { State } from '../ngrx/state';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import { GetUscisEditionDates, PutUscisEditionDates } from '../ngrx/uscis-edition-dates/uscis-edition-dates.actions';
import { RequestState } from '../ngrx/utils';
import { UscisEditionDatesModel } from '../models/uscis-edition-dates.model';
import { selectUscisEditionDatesGetRequestState, selectUscisEditionDatesPutRequestState } from '../ngrx/uscis-edition-dates-requests/state';
import {
  getUscisEditionDates,
  getUscisEditionDatesEntities
} from '../ngrx/uscis-edition-dates/uscis-edition-dates.state';

@Injectable()
export class UscisEditionDatesService {

  getUscisEditionDatesRequest$: Observable<RequestState<UscisEditionDatesModel[]>>;
  uscisEditionDates$: Observable<UscisEditionDatesModel[]>;
  uscisEditionDatesEntities$: Observable<any>;
  putUscisEditionDatesRequest$: Observable<RequestState<UscisEditionDatesModel[]>>;

  constructor(
    private store: Store<State>,
  ) {
    this.uscisEditionDates$ = this.store.pipe(select(getUscisEditionDates));
    this.uscisEditionDatesEntities$ = this.store.pipe(select(getUscisEditionDatesEntities));
    this.getUscisEditionDatesRequest$ = this.store.pipe(select(selectUscisEditionDatesGetRequestState));
    this.putUscisEditionDatesRequest$ = this.store.pipe(select(selectUscisEditionDatesPutRequestState));

  }

  getUscisEditionDates(params?: any) {
    this.store.dispatch(new GetUscisEditionDates(params));
    return this.getUscisEditionDatesRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updateUscisEditionDates(uscisEditionDates) {
    this.store.dispatch(new PutUscisEditionDates(uscisEditionDates));
    return this.putUscisEditionDatesRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }
}
