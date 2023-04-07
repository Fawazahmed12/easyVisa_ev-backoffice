import { Action } from '@ngrx/store';

import { EmailTemplateTypes } from '../../../core/models/email-template-types.enum';
import { Reminder } from '../../models/reminder.model';
import { REMINDERS } from './reminders.state';


export const RemindersActionTypes = {
  GetReminders: `[${REMINDERS}] Get Reminders`,
  GetRemindersSuccess: `[${REMINDERS}] Get Reviews Success`,
  GetRemindersFailure: `[${REMINDERS}] Get Reviews Failure`,
  PatchReminders: `[${REMINDERS}] Put Review`,
  PatchRemindersSuccess: `[${REMINDERS}] Put Review Success`,
  PatchRemindersFailure: `[${REMINDERS}] Put Review Failure`,
  SetActiveReminderType: `[${REMINDERS}] Set Active ReminderType`,
};


export class GetReminders implements Action {
  readonly type = RemindersActionTypes.GetReminders;

  constructor(public payload: number) {
  }
}

export class GetRemindersSuccess implements Action {
  readonly type = RemindersActionTypes.GetRemindersSuccess;

  constructor(public payload: Reminder[]) {
  }
}

export class GetRemindersFailure implements Action {
  readonly type = RemindersActionTypes.GetRemindersFailure;

  constructor(public payload?: any) {
  }
}

export class PatchReminders implements Action {
  readonly type = RemindersActionTypes.PatchReminders;

  constructor(public payload: {id: number; reminders: Reminder[],activeOrganizationId:string}) {
  }
}

export class PatchRemindersSuccess implements Action {
  readonly type = RemindersActionTypes.PatchRemindersSuccess;

  constructor(public payload: Reminder[]) {
  }
}

export class PatchRemindersFailure implements Action {
  readonly type = RemindersActionTypes.PatchRemindersFailure;

  constructor(public payload?: any) {
  }
}

export class SetActiveReminderType implements Action {
  readonly type = RemindersActionTypes.SetActiveReminderType;

  constructor(public payload: EmailTemplateTypes) {
  }
}


export type RemindersActionsUnion =
  | GetReminders
  | GetRemindersSuccess
  | GetRemindersFailure
  | PatchReminders
  | PatchRemindersSuccess
  | PatchRemindersFailure
  | SetActiveReminderType;
