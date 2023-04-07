import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Alert } from '../../../task-queue/models/alert.model';


export const ALERTS = 'Alerts';

export interface AlertsState extends EntityState<Alert> {
  totalAlerts: string;
  activeAlertId: number;
}

export const adapter: EntityAdapter<Alert> = createEntityAdapter<Alert>();

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectAlerts = selectAll;

export const selectAlertsEntities = selectEntities;

export const selectAlertsState = createFeatureSelector<AlertsState>(ALERTS);

export const selectActiveAlertId = ({activeAlertId}: AlertsState) => activeAlertId;
export const selectTotalAlerts = ({totalAlerts}: AlertsState) => totalAlerts;

export const selectActiveAlert = createSelector(
  selectAlertsEntities,
  selectActiveAlertId,
  (alertsEntities, alertId) => alertsEntities[alertId]
);

export const getAlertsData = createSelector(
  selectAlertsState,
  selectAlerts,
);

export const getAlertsEntities = createSelector(
  selectAlertsState,
  selectAlertsEntities,
);

export const getActiveAlertId = createSelector(
  selectAlertsState,
  selectActiveAlertId,
);

export const getActiveAlert = createSelector(
  selectAlertsState,
  selectActiveAlert,
);

export const getTotalAlerts = createSelector(
  selectAlertsState,
  selectTotalAlerts,
);
