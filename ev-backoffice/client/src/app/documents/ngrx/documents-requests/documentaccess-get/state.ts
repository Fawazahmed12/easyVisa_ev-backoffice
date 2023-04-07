import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { DocumentsRequestService } from '../request.service';

export const documentAccessGetRequestHandler = createRequestHandler('GetDocumentAccessRequest');

export function documentAccessGetRequestReducer(state, action) {
  return documentAccessGetRequestHandler.reducer(state, action);
}

@Injectable()
export class DocumentAccessGetRequestEffects {

  @Effect()
  documentAccessGetData$: Observable<Action> = documentAccessGetRequestHandler.effect(
    this.actions$,
    this.documentsRequestService.documentAccessGetRequest.bind(this.documentsRequestService)
  );

  constructor(
    private actions$: Actions,
    private documentsRequestService: DocumentsRequestService
  ) {
  }
}
