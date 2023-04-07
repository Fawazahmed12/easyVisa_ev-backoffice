import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';

import { createFeatureSelector, createSelector } from '@ngrx/store';

import { UscisEditionDatesModel } from '../../models/uscis-edition-dates.model';

export const USCIS_EDITION_DATES = 'UscisEditionDates';

export type UscisEditionDatesState = EntityState<UscisEditionDatesModel>;

export const adapter: EntityAdapter<UscisEditionDatesModel> = createEntityAdapter<UscisEditionDatesModel>({
  selectId: (entity) => entity.formId
});

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectUscisEditionDates = selectAll;

export const selectUscisEditionDatesEntities = selectEntities;

export const selectUscisEditionDatesState = createFeatureSelector<UscisEditionDatesState>(USCIS_EDITION_DATES);

export const getUscisEditionDates = createSelector(
  selectUscisEditionDatesState,
  selectUscisEditionDates,
);

export const getUscisEditionDatesEntities = createSelector(
  selectUscisEditionDatesState,
  selectUscisEditionDatesEntities,
);
