import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { DocumentsRequestService } from '../request.service';

export const  sentDocumentsGetRequestHandler = createRequestHandler('GetSentDocumentsRequest');

export function sentDocumentsGetRequestReducer(state, action) {
  return sentDocumentsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class SentDocumentsGetRequestEffects {

  @Effect()
  sentDocumentsGetData$: Observable<Action> = sentDocumentsGetRequestHandler.effect(
    this.actions$,
    this.documentsRequestService.sentDocumentsGetRequest.bind(this.documentsRequestService)
  );

  constructor(
    private actions$: Actions,
    private documentsRequestService: DocumentsRequestService
  ) {
  }
}
