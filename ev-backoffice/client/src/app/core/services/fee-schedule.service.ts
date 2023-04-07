import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { filter, share } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { State } from '../ngrx/state';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import { RequestState } from '../ngrx/utils';
import { getFeeScheduleSettings } from '../ngrx/fee-schedule/fee-schedule.state';
import {
  selectFeeScheduleSettingsGetRequestState,
  selectFeeScheduleSettingsPostRequestState
} from '../ngrx/fee-schedule-requests/state';
import { GetFeeScheduleSettings, PostFeeScheduleSettings } from '../ngrx/fee-schedule/fee-schedule.actions';

import { FeeSchedule } from '../models/fee-schedule.model';

@Injectable()
export class FeeScheduleService {

  feeScheduleSettings$: Observable<FeeSchedule[]>;
  feeScheduleSettingsGetRequests$: Observable<RequestState<FeeSchedule[]>>;
  feeScheduleSettingsPostRequests$: Observable<RequestState<FeeSchedule[]>>;

  constructor(
    private store: Store<State>,
  ) {
    this.feeScheduleSettings$ = this.store.pipe(select(getFeeScheduleSettings));
    this.feeScheduleSettingsGetRequests$ = this.store.pipe(select(selectFeeScheduleSettingsGetRequestState));
    this.feeScheduleSettingsPostRequests$ = this.store.pipe(select(selectFeeScheduleSettingsPostRequestState));
  }

  getFeeScheduleSettings() {
    this.store.dispatch(new GetFeeScheduleSettings());
    return this.feeScheduleSettingsGetRequests$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  postFeeScheduleSettings(data) {
    this.store.dispatch(new PostFeeScheduleSettings(data));
    return this.feeScheduleSettingsPostRequests$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }
}
