import { Injectable } from '@angular/core';

import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';

import {
  getActiveNotification,
  getClientProgressSettings, getFullNotificationTypes, getIsActiveNotificationImportantDocument,
  getNotificationsConfigGetRequestState,
  getNotificationsConfigPutRequestState, getNotificationsTypesGetRequestState,
  getTaskQueueSettings
} from '../ngrx/state';
import { NotificationSettings, UnitConfig } from '../models/notification-settings.model';
import {
  GetNotificationsConfig,
  GetNotificationTypes,
  PutNotificationsConfig,
} from '../ngrx/attorney-notifications/attorney-notifications.actions';
import { NotificationCategory, NotificationTypes } from '../models/notyfication-types.model';


@Injectable()
export class AttorneyNotificationsService {
  taskQueueSettings$: Observable<UnitConfig[]>;
  clientProgressSettings$: Observable<UnitConfig[]>;
  notificationTypes$: Observable<NotificationTypes>;
  activeDeadLineReminderItem$: Observable<NotificationCategory>;
  activeNotification$: Observable<NotificationCategory>;
  isActiveNotificationImportantDocument$: Observable<boolean>;
  notificationsConfigGetState$: Observable<RequestState<NotificationSettings>>;
  notificationsConfigPutState$: Observable<RequestState<NotificationSettings>>;
  notificationTypesGetState$: Observable<RequestState<NotificationTypes>>;

  constructor(
    private store: Store<State>
  ) {
    this.taskQueueSettings$ = this.store.pipe(select(getTaskQueueSettings));
    this.clientProgressSettings$ = this.store.pipe(select(getClientProgressSettings));
    this.notificationTypes$ = this.store.pipe(select(getFullNotificationTypes));
    this.activeNotification$ = this.store.pipe(select(getActiveNotification));
    this.isActiveNotificationImportantDocument$ = this.store.pipe(select(getIsActiveNotificationImportantDocument));
    this.notificationsConfigGetState$ = this.store.pipe(select(getNotificationsConfigGetRequestState));
    this.notificationsConfigPutState$ = this.store.pipe(select(getNotificationsConfigPutRequestState));
    this.notificationTypesGetState$ = this.store.pipe(select(getNotificationsTypesGetRequestState));
  }

  getNotificationsConfig(data) {
    this.store.dispatch(new GetNotificationsConfig(data));
    return this.notificationsConfigGetState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  putNotificationsConfig(data: {config: NotificationSettings; id: number,activeOrganizationId:string}) {
    this.store.dispatch(new PutNotificationsConfig(data));
    return this.notificationsConfigPutState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  getNotificationTypes() {
    this.store.dispatch(new GetNotificationTypes());
  }
}
