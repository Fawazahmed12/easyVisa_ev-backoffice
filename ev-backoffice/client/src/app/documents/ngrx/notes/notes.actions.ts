import { Action } from '@ngrx/store';

import { Note } from '../../models/note.model';

import { NOTES } from './notes.state';


export const NotesActionTypes = {
  GetNotes: `[${NOTES}] Get Public Notes`,
  GetNotesSuccess: `[${NOTES}] Get Public Notes Success`,
  GetNotesFailure: `[${NOTES}] Get Public Notes Failure`,
  PostNote: `[${NOTES}] Post Note`,
  PostNoteSuccess: `[${NOTES}] Post Note Success`,
  PostNoteFailure: `[${NOTES}] Post Note Failure`,
  DeleteNote: `[${NOTES}] Delete Note`,
  DeleteNoteSuccess: `[${NOTES}] Delete Note Success`,
  DeleteNoteFailure: `[${NOTES}] Delete Note Failure`,
};


export class GetNotes implements Action {
  readonly type = NotesActionTypes.GetNotes;

  constructor(public payload: number) {
  }
}

export class GetNotesSuccess implements Action {
  readonly type = NotesActionTypes.GetNotesSuccess;

  constructor(public payload: Note[]) {
  }
}

export class GetNotesFailure implements Action {
  readonly type = NotesActionTypes.GetNotesFailure;

  constructor(public payload: any) {
  }
}

export class PostNote implements Action {
  readonly type = NotesActionTypes.PostNote;

  constructor(public payload: Note) {
  }
}

export class PostNoteSuccess implements Action {
  readonly type = NotesActionTypes.PostNoteSuccess;

  constructor(public payload: Note) {
  }
}

export class PostNoteFailure implements Action {
  readonly type = NotesActionTypes.PostNoteFailure;

  constructor(public payload: any) {
  }
}

export class DeleteNote implements Action {
  readonly type = NotesActionTypes.DeleteNote;

  constructor(public payload: Note) {
  }
}

export class DeleteNoteSuccess implements Action {
  readonly type = NotesActionTypes.DeleteNoteSuccess;

  constructor(public payload: {id: number}) {
  }
}

export class DeleteNoteFailure implements Action {
  readonly type = NotesActionTypes.DeleteNoteFailure;

  constructor(public payload: any) {
  }
}


export type NotesActionsUnion =
  | GetNotes
  | GetNotesSuccess
  | GetNotesFailure
  | PostNote
  | PostNoteSuccess
  | PostNoteFailure
  | DeleteNote
  | DeleteNoteSuccess
  | DeleteNoteFailure;
