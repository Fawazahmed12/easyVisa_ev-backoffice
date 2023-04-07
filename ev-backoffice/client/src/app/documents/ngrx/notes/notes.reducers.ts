import { Note } from '../../models/note.model';
import { NoteTypes } from '../../models/note-types.enum';

import { adapter, NotesState } from './notes.state';
import { DeleteNoteSuccess, GetNotesSuccess, NotesActionsUnion, NotesActionTypes, PostNoteSuccess } from './notes.actions';
import { PackagesActionTypes } from '../../../core/ngrx/packages/packages.actions';


export const initialState: NotesState = adapter.getInitialState({
  publicNotesId: null,
  representativeNotesId: null,
});


export function reducer(state = initialState, action: NotesActionsUnion) {
  switch (action.type) {

    case NotesActionTypes.GetNotesSuccess: {
      const payload: Note[] = (action as GetNotesSuccess).payload;
      const publicNotes = payload.filter((note) => note.documentNoteType === NoteTypes.PUBLIC_NOTE);
      const representativeNotes = payload.filter((note) => note.documentNoteType === NoteTypes.REPRESENTATIVE_NOTE);

      return {
        ...adapter.setAll(payload, state),
        publicNotesId: publicNotes.map((note) => note.id),
        representativeNotesId: representativeNotes.map((note) => note.id),
      };
    }

    case NotesActionTypes.PostNoteSuccess: {
      const payload: Note = (action as PostNoteSuccess).payload;
      const id: number = payload.id;
      const isPubic = payload.documentNoteType === NoteTypes.PUBLIC_NOTE;
      const representativeNotesId = isPubic ? state.representativeNotesId : [...state.representativeNotesId, id];
      const publicNotesId = !isPubic ? state.publicNotesId : [...state.publicNotesId, id];

      return {
        ...state,
        ...adapter.upsertOne(payload, state),
        representativeNotesId,
        publicNotesId,
      };
    }

    case NotesActionTypes.DeleteNoteSuccess: {
      const payload = (action as DeleteNoteSuccess).payload;
      const representativeNotesId = state.representativeNotesId.filter((id) => id !== payload.id);
      const publicNotesId = state.publicNotesId.filter((id) => id !== payload.id);

      return {
        ...adapter.removeOne(payload.id, state),
        representativeNotesId,
        publicNotesId,
      };
    }

    case PackagesActionTypes.ClearActivePackage: {
      return{
        ...state,
        publicNotesId: null,
        representativeNotesId: null,
      };
    }

    default: {
      return state;
    }
  }
}
