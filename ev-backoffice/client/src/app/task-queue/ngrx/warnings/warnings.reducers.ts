import { adapter, WarningsState } from './warnings.state';
import { GetWarningsSuccess, PutWarningSuccess, SetActiveWarning, WarningsActionsUnion, WarningsActionTypes } from './warnings.actions';

export const initialState: WarningsState = adapter.getInitialState({
  activeWarningId: null,
  totalWarnings: null,
});

export function reducer(state = initialState, action: WarningsActionsUnion) {
  switch (action.type) {

    case WarningsActionTypes.GetWarningsSuccess: {
      const payload = (action as GetWarningsSuccess).payload;

      return {
        ...adapter.setAll(payload.body, state),
        activeWarningId: null,
        totalWarnings: payload.xTotalCount,
      };
    }

    case WarningsActionTypes.GetWarnings: {
      return {
        ...adapter.removeAll(state),
        activeWarningId: null,
        totalWarnings: null,
      };
    }

    case WarningsActionTypes.PutWarningSuccess: {
      return {
        ...adapter.upsertOne((action as PutWarningSuccess).payload, state),
      };
    }

    case WarningsActionTypes.DeleteWarningsSuccess: {
      const foundedActiveWarningId = action.payload.deletedWarningIds.find((id) => id === state.activeWarningId);

      return {
        ...adapter.removeMany(action.payload.deletedWarningIds, state),
        activeWarningId: foundedActiveWarningId ? null : state.activeWarningId,
      };
    }

    case WarningsActionTypes.SetActiveWarning: {
      return {
        ...state,
        activeWarningId: (action as SetActiveWarning).payload,
      };
    }

    default: {
      return state;
    }
  }
}
