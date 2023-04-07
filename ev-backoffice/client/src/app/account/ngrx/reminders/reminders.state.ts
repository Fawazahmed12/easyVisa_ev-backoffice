import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector, createSelector } from '@ngrx/store';

import { EmailTemplateTypes } from '../../../core/models/email-template-types.enum';

import { Reminder } from '../../models/reminder.model';

export const REMINDERS = 'Reminders';

export interface RemindersState extends EntityState<Reminder> {
  activeItemType: EmailTemplateTypes;
}

export const  adapter: EntityAdapter<Reminder> = createEntityAdapter<Reminder>({
  selectId: (entity) => entity.templateType
});

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectReminders = selectAll;

export const selectRemindersEntities = selectEntities;

export const selectRemindersState = createFeatureSelector<RemindersState>(REMINDERS);

export const selectActiveItemType = ({activeItemType}: RemindersState) => activeItemType;

export const selectActiveItem = createSelector(
  selectRemindersEntities,
  selectActiveItemType,
  (remindersEntities, reminderType) => remindersEntities[reminderType]
);


