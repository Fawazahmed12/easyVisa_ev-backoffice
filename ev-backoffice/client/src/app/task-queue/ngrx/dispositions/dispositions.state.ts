import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Disposition } from '../../models/dispositions.model';

export const DISPOSITIONS = 'Dispositions';

export interface DispositionsState extends EntityState<Disposition> {
  activeDispositionId: number;
  activeDispositionData: any;
  totalDispositions: string;
}

export const adapter: EntityAdapter<Disposition> = createEntityAdapter<Disposition>();

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectDispositions = selectAll;

export const selectDispositionsEntities = selectEntities;

export const selectDispositionsState = createFeatureSelector<DispositionsState>(DISPOSITIONS);

export const selectActiveDispositionId = ({activeDispositionId}: DispositionsState) => activeDispositionId;
export const selectDispositionData = ({activeDispositionData}: DispositionsState) => activeDispositionData;
export const selectTotalDispositions = ({totalDispositions}: DispositionsState) => totalDispositions;
export const selectActiveDisposition = createSelector(
  selectDispositionsEntities,
  selectActiveDispositionId,
  (dispositionsEntities, dispositionId) => dispositionsEntities[dispositionId]
);
