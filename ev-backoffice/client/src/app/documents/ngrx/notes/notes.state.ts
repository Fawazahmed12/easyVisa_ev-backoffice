import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector, createSelector } from '@ngrx/store';

import { Note } from '../../models/note.model';

export const NOTES = 'Notes';

export interface NotesState extends EntityState<Note> {
  publicNotesId: number[];
  representativeNotesId: number[];
}

export const adapter: EntityAdapter<Note> = createEntityAdapter<Note>();

export const {selectAll, selectEntities} = adapter.getSelectors();

export const selectNotes = selectAll;

export const selectNotesEntities = selectEntities;

export const selectNotesState = createFeatureSelector<NotesState>(NOTES);

export const selectPublicNotesId = (state: NotesState) => state.publicNotesId;

export const selectRepresentativeNotesId = (state: NotesState) => state.representativeNotesId;

export const selectRepresentativeNotes = createSelector(
  selectNotesEntities,
  selectRepresentativeNotesId,
  (notesEntities, representativeNotesIds) => representativeNotesIds && representativeNotesIds.map((id) => notesEntities[id])
);

export const selectPublicNotes = createSelector(
  selectNotesEntities,
  selectPublicNotesId,
  (notesEntities, publicNotesIds) => publicNotesIds && publicNotesIds.map((id) => notesEntities[id])
);


