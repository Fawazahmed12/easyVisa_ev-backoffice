import { adapter, AlertsState } from './alerts.state';
import { AlertsActionsUnion, AlertsActionTypes, GetAlertsSuccess, PutAlertSuccess, SetActiveAlert } from './alerts.actions';

export const initialState: AlertsState = adapter.getInitialState({
  activeAlertId: null,
  totalAlerts: null,
});

export function reducer(state = initialState, action: AlertsActionsUnion) {
  switch (action.type) {

    case AlertsActionTypes.GetAlertsSuccess: {
      const payload = (action as GetAlertsSuccess).payload;
      return {
        ...adapter.setAll(payload.body, state),
        activeAlertId: null,
        totalAlerts: payload.xTotalCount,
      };
    }

    case AlertsActionTypes.PutAlertSuccess: {
      return {
        ...adapter.upsertOne((action as PutAlertSuccess).payload, state),
      };
    }

    case AlertsActionTypes.DeleteAlertsSuccess: {
      const foundedActiveAlertId = action.payload.deletedAlertIds.find((id) => id === state.activeAlertId);

      return {
        ...adapter.removeMany(action.payload.deletedAlertIds, state),
        activeAlertId: foundedActiveAlertId ? null : state.activeAlertId,
      };
    }

    case AlertsActionTypes.SetActiveAlert: {
      return {
        ...state,
        activeAlertId: (action as SetActiveAlert).payload,
      };
    }

    default: {
      return state;
    }
  }
}
