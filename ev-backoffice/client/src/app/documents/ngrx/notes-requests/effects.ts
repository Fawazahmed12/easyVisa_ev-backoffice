import { NotesGetRequestEffects } from './notes-get/state';
import { NotePostRequestEffects } from './note-post/state';
import { NoteDeleteRequestEffects } from './note-delete/state';


export const NotesRequestEffects = [
  NotesGetRequestEffects,
  NotePostRequestEffects,
  NoteDeleteRequestEffects,
];
