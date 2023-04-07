import { adapter, UscisEditionDatesState } from './uscis-edition-dates.state';

import {
  UscisEditionDatesActionsUnion,
  UscisEditionDatesActionTypes,
  GetUscisEditionDatesSuccess, PutUscisEditionDatesSuccess
} from './uscis-edition-dates.actions';

export const initialState: UscisEditionDatesState = adapter.getInitialState({

});

export function reducer(state = initialState, action: UscisEditionDatesActionsUnion) {
  switch (action.type) {

    case UscisEditionDatesActionTypes.GetUscisEditionDatesSuccess: {
      return {
        ...adapter.setAll((action as GetUscisEditionDatesSuccess).payload, state),
      };
    }

    case UscisEditionDatesActionTypes.PutUscisEditionDatesSuccess: {
      return {
        ...adapter.upsertMany((action as PutUscisEditionDatesSuccess).payload, state),
      };
    }

    default: {
      return state;
    }
  }
}
