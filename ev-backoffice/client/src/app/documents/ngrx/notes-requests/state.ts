import { createFeatureSelector } from '@ngrx/store';
import { RequestState } from '../../../core/ngrx/utils';
import { Note } from '../../models/note.model';


export const NOTES_REQUESTS = 'NotesRequests';

export interface NotesRequestState {
  notesGet?: RequestState<Note[]>;
  notePost?: RequestState<Note>;
  noteDelete?: RequestState<{id: number}>;
}

export const selectNoteModuleRequestsState = createFeatureSelector<NotesRequestState>(NOTES_REQUESTS);

export const selectNotesGetRequestState = (state: NotesRequestState) => state.notesGet;
export const selectNotePostRequestState = (state: NotesRequestState) => state.notePost;
export const selectNoteDeleteRequestState = (state: NotesRequestState) => state.noteDelete;


export { notesGetRequestHandler } from './notes-get/state';
export { notePostRequestHandler } from './note-post/state';
export { noteDeleteRequestHandler } from './note-delete/state';

