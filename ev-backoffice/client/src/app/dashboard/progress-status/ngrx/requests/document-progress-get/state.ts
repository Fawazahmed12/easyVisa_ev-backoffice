import { Injectable } from '@angular/core';

import { Action, } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../../core/ngrx/utils';
import { ProgressStatusesModuleRequestService } from '../request.service';

export const documentProgressGetRequestHandler = createRequestHandler('GetDocumentProgress');

export function documentProgressGetRequestReducer(state, action) {
  return documentProgressGetRequestHandler.reducer(state, action);
}

@Injectable()
export class DocumentProgressGetRequestEffects {

  @Effect()
  documentProgress$: Observable<Action> = documentProgressGetRequestHandler.effect(
    this.actions$,
    this.progressStatusesModuleRequestService.documentProgressGetRequest.bind(this.progressStatusesModuleRequestService)
  );

  constructor(
    private actions$: Actions,
    private progressStatusesModuleRequestService: ProgressStatusesModuleRequestService,
  ) {
  }
}
