import { createEntityAdapter, Dictionary, EntityAdapter, EntityState } from '@ngrx/entity';

import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Attorney, AttorneyMenu } from '../../models/attorney.model';
import { FeeSchedule } from '../../models/fee-schedule.model';

export const REPRESENTATIVES = 'Representatives';

export interface RepresentativesState extends EntityState<Attorney> {
  currentRepresentativeId: number;
  currentRepresentativeFeeSchedule: FeeSchedule[];
  representativesMenu: AttorneyMenu[];
  feeScheduleIds: number[];
  feeScheduleEntities: Dictionary<FeeSchedule[]>;
}

export const adapter: EntityAdapter<Attorney> = createEntityAdapter<Attorney>();

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectRepresentatives = selectAll;

export const selectRepresentativeEntities = selectEntities;

export const selectRepresentativesState = createFeatureSelector<RepresentativesState>(REPRESENTATIVES);

export const getRepresentatives = createSelector(
  selectRepresentativesState,
  selectRepresentatives,
);

export const getRepresentativeEntities = createSelector(
  selectRepresentativesState,
  selectRepresentativeEntities,
);

export const getCurrentRepresentative = createSelector(
  selectRepresentativesState,
  (state: RepresentativesState) => state.representativesMenu ?
    state.representativesMenu.find((representative) =>
    representative.id === state.currentRepresentativeId)
    : null
);

export const getCurrentRepresentativeUserId = createSelector(
  selectRepresentativesState,
  (state: RepresentativesState) => {
    const currentRepresentative = state.representativesMenu ?
      state.representativesMenu.find((representative) => representative.id === state.currentRepresentativeId)
      : null;
    return currentRepresentative && currentRepresentative.userId ? currentRepresentative.userId : null;
  }
);

export const getCurrentRepresentativeId = createSelector(
  selectRepresentativesState,
  (state: RepresentativesState) => state.currentRepresentativeId,
);

export const getRepresentativesMenu = createSelector(
  selectRepresentativesState,
  (state: RepresentativesState) => state.representativesMenu,
);

export const getCurrentRepresentativeFeeSchedule = createSelector(
  selectRepresentativesState,
  (state: RepresentativesState) => state.currentRepresentativeFeeSchedule,
);

export const getFeeScheduleEntities = createSelector(
  selectRepresentativesState,
  (state: RepresentativesState) => state.feeScheduleEntities || null,
);



