import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { NotesRequestService } from '../request.service';


export const noteDeleteRequestHandler = createRequestHandler('Note Delete');

export function noteDeleteRequestReducer(state, action) {
  return noteDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class NoteDeleteRequestEffects {

  @Effect()
  noteDelete$: Observable<Action> = noteDeleteRequestHandler.effect(
    this.actions$,
    this.notesRequestService.noteDeleteRequest.bind(this.notesRequestService)
  );

  constructor(
    private actions$: Actions,
    private notesRequestService: NotesRequestService
  ) {
  }
}
