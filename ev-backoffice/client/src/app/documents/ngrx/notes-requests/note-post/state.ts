import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { createRequestHandler } from '../../../../core/ngrx/utils';

import { NotesRequestService } from '../request.service';


export const notePostRequestHandler = createRequestHandler('Note Post');

export function notePostRequestReducer(state, action) {
  return notePostRequestHandler.reducer(state, action);
}

@Injectable()
export class NotePostRequestEffects {

  @Effect()
  notePost$: Observable<Action> = notePostRequestHandler.effect(
    this.actions$,
    this.notesRequestService.notePostRequest.bind(this.notesRequestService)
  );

  constructor(
    private actions$: Actions,
    private notesRequestService: NotesRequestService
  ) {
  }
}
