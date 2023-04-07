import { NotesRequestState } from './state';
import { notesGetRequestReducer } from './notes-get/state';
import { notePostRequestReducer } from './note-post/state';
import { noteDeleteRequestReducer } from './note-delete/state';


export function reducer(state: NotesRequestState = {}, action): NotesRequestState {
  return {
    notesGet: notesGetRequestReducer(state.notesGet, action),
    notePost: notePostRequestReducer(state.notePost, action),
    noteDelete: noteDeleteRequestReducer(state.noteDelete, action),
  };
}
