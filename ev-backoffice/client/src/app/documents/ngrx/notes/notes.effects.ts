import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { map, tap } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';

import { Note } from '../../models/note.model';

import { notesGetRequestHandler } from '../notes-requests/notes-get/state';
import { notePostRequestHandler } from '../notes-requests/note-post/state';
import { noteDeleteRequestHandler } from '../notes-requests/note-delete/state';

import {
  DeleteNote,
  DeleteNoteFailure,
  DeleteNoteSuccess,
  GetNotes,
  GetNotesFailure,
  GetNotesSuccess,
  NotesActionTypes,
  PostNote,
  PostNoteFailure,
  PostNoteSuccess
} from './notes.actions';

@Injectable()
export class NotesEffects {

  @Effect()
  getNotes$: Observable<Action> = this.actions$.pipe(
    ofType(NotesActionTypes.GetNotes),
    map(({ payload }: GetNotes) => notesGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getNotesSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(notesGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<Note[]>) => new GetNotesSuccess(payload))
  );

  @Effect()
  getNotesFailure$: Observable<Action> = this.actions$.pipe(
    ofType(notesGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetNotesFailure(payload))
  );

  @Effect()
  postNote$: Observable<Action> = this.actions$.pipe(
    ofType(NotesActionTypes.PostNote),
    map(({ payload }: PostNote) => notePostRequestHandler.requestAction(payload))
  );

  @Effect()
  postNoteSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(notePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<Note>) => new PostNoteSuccess(payload))
  );

  @Effect()
  postNoteFailure$: Observable<Action> = this.actions$.pipe(
    ofType(notePostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new PostNoteFailure(payload))
  );

  @Effect()
  deleteNote$: Observable<Action> = this.actions$.pipe(
    ofType(NotesActionTypes.DeleteNote),
    map(({ payload }: DeleteNote) => noteDeleteRequestHandler.requestAction(payload))
  );

  @Effect()
  deleteNoteSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(noteDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<{id: number}>) => new DeleteNoteSuccess(payload))
  );

  @Effect()
  deleteNoteFailure$: Observable<Action> = this.actions$.pipe(
    ofType(noteDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new DeleteNoteFailure(payload))
  );


  constructor(private actions$: Actions) {
  }
}
