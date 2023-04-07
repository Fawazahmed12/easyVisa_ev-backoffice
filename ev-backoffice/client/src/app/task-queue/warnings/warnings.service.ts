import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { Dictionary } from '@ngrx/entity';
import { select, Store } from '@ngrx/store';

import { RequestState } from '../../core/ngrx/utils';
import { State } from '../../core/ngrx/state';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import { Warning } from '../models/warning.model';
import {
  getActiveWarning,
  getActiveWarningId, getTotalWarnings,
  getWarningPutRequestState,
  getWarningsData,
  getWarningsDeleteRequestState,
  getWarningsEntities,
  getWarningsGetRequestState
} from '../ngrx/state';
import { DeleteWarnings, GetWarnings, PutWarning, SetActiveWarning } from '../ngrx/warnings/warnings.actions';


@Injectable()
export class WarningsService {
  getWarningsGetRequest$: Observable<RequestState<Warning[]>>;
  getWarningsDeleteRequest$: Observable<RequestState<number[]>>;
  getWarningPutRequest$: Observable<RequestState<Warning>>;
  warnings$: Observable<Warning[]>;
  activeWarning$: Observable<Warning>;
  activeWarningId$: Observable<number>;
  warningsEntities$: Observable<Dictionary<Warning>>;
  totalWarnings$: Observable<string>;


  constructor(
    private store: Store<State>,
  ) {
    this.warnings$ = this.store.pipe(select(getWarningsData));
    this.totalWarnings$ = this.store.pipe(select(getTotalWarnings));
    this.activeWarning$ = this.store.pipe(select(getActiveWarning));
    this.activeWarningId$ = this.store.pipe(select(getActiveWarningId));
    this.warningsEntities$ = this.store.pipe(select(getWarningsEntities));
    this.getWarningsGetRequest$ = this.store.pipe(select(getWarningsGetRequestState));
    this.getWarningsDeleteRequest$ = this.store.pipe(select(getWarningsDeleteRequestState));
    this.getWarningPutRequest$ = this.store.pipe(select(getWarningPutRequestState));
  }

  getWarnings(params) {
    this.store.dispatch(new GetWarnings(params));
    return this.getWarningsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updateWarning(warning) {
    this.store.dispatch(new PutWarning(warning));
    return this.getWarningPutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  removeWarnings(params) {
    this.store.dispatch(new DeleteWarnings(params));
    return this.getWarningsDeleteRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  setActiveWarning(id) {
    this.store.dispatch(new SetActiveWarning(id));
  }

}
