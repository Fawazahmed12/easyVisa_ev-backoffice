import {
  GetRemindersSuccess,
  PatchRemindersSuccess,
  RemindersActionsUnion,
  RemindersActionTypes,
  SetActiveReminderType,
} from './reminders.actions';
import { adapter, RemindersState } from './reminders.state';

export const initialState: RemindersState = adapter.getInitialState({
  activeItemType: null,
});

export function reducer(state = initialState, action: RemindersActionsUnion) {
  switch (action.type) {

    case RemindersActionTypes.GetRemindersSuccess: {
      return {
        ...adapter.setAll((action as GetRemindersSuccess).payload, state),
      };
    }

    case RemindersActionTypes.PatchRemindersSuccess: {
      return {
        ...state,
        ...adapter.upsertMany((action as PatchRemindersSuccess).payload, state),
      };
    }

    case RemindersActionTypes.SetActiveReminderType: {

      return {
        ...state,
        activeItemType: (action as SetActiveReminderType).payload,
      };
    }


    default: {
      return state;
    }
  }
}
