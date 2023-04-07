import { Injectable } from '@angular/core';

import { Dictionary } from '@ngrx/entity';
import { select, Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';
import { EmailTemplateTypes } from '../../core/models/email-template-types.enum';

import {
  getActiveItem,
  getReminders, getRemindersEntities, getRemindersGetRequestState, getRemindersPatchRequestState
} from '../ngrx/state';
import {
  GetReminders,
  PatchReminders, SetActiveReminderType,
} from '../ngrx/reminders/reminders.actions';
import { Reminder } from '../models/reminder.model';


@Injectable()
export class RemindersService {
  reminders$: Observable<Reminder[]>;
  remindersEntities$: Observable<Dictionary<Reminder>>;
  activeDeadlineReminder$: Observable<Reminder>;
  activeItem$: Observable<Reminder>;
  remindersGetState$: Observable<RequestState<Reminder[]>>;
  remindersPatchState$: Observable<RequestState<Reminder[]>>;

  constructor(
    private store: Store<State>
  ) {
    this.reminders$ = this.store.pipe(select(getReminders));
    this.remindersEntities$ = this.store.pipe(select(getRemindersEntities));
    this.activeItem$ = this.store.pipe(select(getActiveItem));
    this.remindersGetState$ = this.store.pipe(select(getRemindersGetRequestState));
    this.remindersPatchState$ = this.store.pipe(select(getRemindersPatchRequestState));
  }

  getReminders(id: number) {
    this.store.dispatch(new GetReminders(id));
    return this.remindersGetState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  patchReminders(data: { id: number; reminders: Reminder[],activeOrganizationId:string }) {
    this.store.dispatch(new PatchReminders(data));
    return this.remindersPatchState$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share()
    );
  }

  setActiveReminderType(emailTemplate: EmailTemplateTypes) {
    this.store.dispatch(new SetActiveReminderType(emailTemplate));
  }
}
