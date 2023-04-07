import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Warning } from '../../models/warning.model';

export const WARNINGS = 'Warnings';

export interface WarningsState extends EntityState<Warning> {
  activeWarningId: number;
  totalWarnings: string;
}

export const adapter: EntityAdapter<Warning> = createEntityAdapter<Warning>();

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectWarnings = selectAll;

export const selectWarningsEntities = selectEntities;

export const selectWarningsState = createFeatureSelector<WarningsState>(WARNINGS);

export const selectActiveWarningId = ({activeWarningId}: WarningsState) => activeWarningId;
export const selectTotalWarnings = ({totalWarnings}: WarningsState) => totalWarnings;

export const selectActiveWarning = createSelector(
  selectWarningsEntities,
  selectActiveWarningId,
  (warningEntities, warningId) => warningEntities[warningId]
);
