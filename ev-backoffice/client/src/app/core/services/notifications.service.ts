import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';

import { Observable, Subject } from 'rxjs';
import { filter, share } from 'rxjs/operators';
import { CookieService } from 'ngx-cookie-service';

import { State } from '../ngrx/state';
import { getTaskQueueCount, getTaskQueueNotifications } from '../ngrx/notifications/notifications.state';
import {
  DecreaseAlertsCount,
  DecreaseWarningsCount,
  GetTaskQueueCounts
} from '../ngrx/notifications/notifications.actions';
import { throwIfRequestFailError } from '../ngrx/utils/rxjs-utils';
import { TaskQueueCounts } from '../models/task-queue-counts.model';
import { RequestState } from '../ngrx/utils';
import { selectTaskQueueCountsGetRequestState } from '../ngrx/notifications-requests/state';

export const isShowedPaymentWarning = 'ev-is-showed-payment-warning';

@Injectable()
export class NotificationsService {

  taskQueueCount$: Observable<number>;
  taskQueueNotifications$: Observable<TaskQueueCounts>;
  getTaskQueueCountsRequest$: Observable<RequestState<TaskQueueCounts>>;
  showComponent$: Subject<any> = new Subject<any>();

  constructor(
    private store: Store<State>,
    private cookieService: CookieService,
  ) {
    this.taskQueueCount$ = this.store.pipe(select(getTaskQueueCount));
    this.taskQueueNotifications$ = this.store.pipe(select(getTaskQueueNotifications));
    this.getTaskQueueCountsRequest$ = this.store.pipe(select(selectTaskQueueCountsGetRequestState));
  }

  getTaskQueueNotifications(data) {
    this.store.dispatch(new GetTaskQueueCounts(data));
    return this.getTaskQueueCountsRequest$.pipe(
      filter((state) => !state.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  decreaseAlertsNotifications() {
    this.store.dispatch(new DecreaseAlertsCount());
  }

  decreaseWarningsNotifications() {
    this.store.dispatch(new DecreaseWarningsCount());
  }

  getIsShowedPaymentWarning() {
    return this.cookieService.get(isShowedPaymentWarning) === 'true';
  }

  setIsShowedPaymentWarning(data) {
    this.cookieService.set(isShowedPaymentWarning, data, 1, '/', null, null, 'Strict');
  }

  removeIsShowedPaymentWarning() {
    // TODO: used to remove the cookie. remove when update to new version of ngx-cookie-service
    this.cookieService.set(isShowedPaymentWarning, '', -1, '/', null, null, 'Strict');
  }
}
