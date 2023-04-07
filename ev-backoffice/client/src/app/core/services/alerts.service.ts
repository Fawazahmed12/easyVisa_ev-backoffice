import { Injectable } from '@angular/core';

import { filter, share } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { select, Store } from '@ngrx/store';
import { Dictionary } from '@ngrx/entity';

import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import { State } from '../ngrx/state';
import { RequestState } from '../ngrx/utils';

import { Alert } from '../../task-queue/models/alert.model';

import { DeleteAlerts, GetAlerts, PostAlert, PutAlert, SetActiveAlert } from '../ngrx/alerts/alerts.actions';
import { sendAlertPostRequestHandler } from '../ngrx/alerts-requests/send-alert-post/state';
import {
  selectAlertPutRequestState,
  selectAlertsDeleteRequestState,
  selectAlertsGetRequestState,
  selectSendAlertPostRequestState
} from '../ngrx/alerts-requests/state';
import { getActiveAlert, getActiveAlertId, getAlertsData, getAlertsEntities, getTotalAlerts } from '../ngrx/alerts/alerts.state';

@Injectable()
export class AlertsService {
  getAlertsGetRequest$: Observable<RequestState<Alert[]>>;
  getAlertsDeleteRequest$: Observable<RequestState<number[]>>;
  getAlertPutRequest$: Observable<RequestState<Alert>>;
  getSendAlertPostRequest$: Observable<RequestState<any>>;
  alerts$: Observable<Alert[]>;
  activeAlert$: Observable<Alert>;
  activeAlertId$: Observable<number>;
  alertsEntities$: Observable<Dictionary<Alert>>;
  totalAlerts$: Observable<string>;

  constructor(
    private store: Store<State>,
  ) {
    this.alerts$ = this.store.pipe(select(getAlertsData));
    this.totalAlerts$ = this.store.pipe(select(getTotalAlerts));
    this.activeAlert$ = this.store.pipe(select(getActiveAlert));
    this.activeAlertId$ = this.store.pipe(select(getActiveAlertId));
    this.alertsEntities$ = this.store.pipe(select(getAlertsEntities));
    this.getAlertsGetRequest$ = this.store.pipe(select(selectAlertsGetRequestState));
    this.getAlertsDeleteRequest$ = this.store.pipe(select(selectAlertsDeleteRequestState));
    this.getAlertPutRequest$ = this.store.pipe(select(selectAlertPutRequestState));
    this.getSendAlertPostRequest$ = this.store.pipe(select(selectSendAlertPostRequestState));
  }

  getAlerts(params) {
    this.store.dispatch(new GetAlerts(params));
    return this.getAlertsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  updateAlert(alert) {
    this.store.dispatch(new PutAlert(alert));
    return this.getAlertPutRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  removeAlerts(params) {
    this.store.dispatch(new DeleteAlerts(params));
    return this.getAlertsDeleteRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  setActiveAlert(id) {
    this.store.dispatch(new SetActiveAlert(id));
  }

  sendAlert(alert: Alert) {
    this.store.dispatch(new PostAlert(alert));
    return this.getSendAlertPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }
}
