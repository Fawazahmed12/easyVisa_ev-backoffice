import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';


import { NotesRequestService } from '../request.service';
import { createRequestHandler } from '../../../../core/ngrx/utils';


export const notesGetRequestHandler = createRequestHandler('Notes Get');

export function notesGetRequestReducer(state, action) {
  return notesGetRequestHandler.reducer(state, action);
}

@Injectable()
export class NotesGetRequestEffects {

  @Effect()
  notesGet$: Observable<Action> = notesGetRequestHandler.effect(
    this.actions$,
    this.notesRequestService.notesGetRequest.bind(this.notesRequestService)
  );

  constructor(
    private actions$: Actions,
    private notesRequestService: NotesRequestService
  ) {
  }
}
