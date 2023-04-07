import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector } from '@ngrx/store';

import { MilestoneDate } from '../../models/milestone-date.model';

export const MILESTONE_DATES = 'MilestoneDates';

export type MilestoneDatesState = EntityState<MilestoneDate>;

export const adapter: EntityAdapter<MilestoneDate> = createEntityAdapter<MilestoneDate>( {
  selectId: (myEntity: MilestoneDate) => myEntity.milestoneTypeId
});

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectMilestoneDatesState = createFeatureSelector<MilestoneDatesState>(MILESTONE_DATES);

export const selectMilestoneDates = selectAll;

export const selectMilestoneDatesEntities = selectEntities;
