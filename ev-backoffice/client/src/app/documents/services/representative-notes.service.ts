import { Injectable } from '@angular/core';

import { Action, select, Store } from '@ngrx/store';
import { filter, share } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';
import { RequestState } from '../../core/ngrx/utils';
import { State } from '../../core/ngrx/state';

import { DeleteNote, GetNotes, PostNote } from '../ngrx/notes/notes.actions';
import { Note } from '../models/note.model';
import {
  getNoteDeleteRequestState,
  getNotePostRequestState,
  getNotesGetRequestState,
  getPublicNotes,
  getRepresentativeNotes,
} from '../ngrx/state';
import { NotesEffects } from '../ngrx/notes/notes.effects';
import { HttpErrorResponse } from '@angular/common/http';
import { ModalService } from '../../core/services';

@Injectable()
export class RepresentativeNotesService {
  deleteNoteRequestState$: Observable<RequestState<{ id: number }>>;
  getNotesRequestState$: Observable<RequestState<Note[]>>;
  postNoteRequestState$: Observable<RequestState<Note>>;
  representativeNotes$: Observable<Note[]>;
  publicNotes$: Observable<Note[]>;
  postNoteFailAction$: Observable<Action>;
  deleteNoteFailAction$: Observable<Action>;

  constructor(
    private store: Store<State>,
    private noteEffects: NotesEffects,
    private modalService: ModalService
  ) {
    this.deleteNoteRequestState$ = this.store.pipe(select(getNoteDeleteRequestState));
    this.getNotesRequestState$ = this.store.pipe(select(getNotesGetRequestState));
    this.postNoteRequestState$ = this.store.pipe(select(getNotePostRequestState));
    this.representativeNotes$ = this.store.pipe(select(getRepresentativeNotes));
    this.publicNotes$ = this.store.pipe(select(getPublicNotes));
    this.postNoteFailAction$ = this.noteEffects.postNoteFailure$;
    this.deleteNoteFailAction$ = this.noteEffects.deleteNoteFailure$;
  }

  getNotes(data) {
    this.store.dispatch(new GetNotes(data));
    return this.getNotesRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  postNote(data) {
    this.store.dispatch(new PostNote(data));
    return this.postNoteRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  deleteNote(data) {
    this.store.dispatch(new DeleteNote(data));
    return this.deleteNoteRequestState$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  documentAccessErrorFilter(action) {
    const ACCESS_ERROR_TYPE = 'INVALID_DOCUMENTPORTAL_ACCESS';
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    const errors = payload.error.errors || [ payload.error ];
    const accessError = errors[ 0 ] || { type: '' };
    if (accessError.type !== ACCESS_ERROR_TYPE) {
      return false;
    }
    const errorMessages = accessError.message.split('|');
    const errorMessage = errorMessages[ 0 ];
    const sourceFieldId = errorMessages[ 1 ];
    accessError.message = errorMessage;
    return false;
  }

  documentAccessErrorHandler(action) {
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    this.modalService.showErrorModal(payload.error.errors || [ payload.error ]);
  }

}
