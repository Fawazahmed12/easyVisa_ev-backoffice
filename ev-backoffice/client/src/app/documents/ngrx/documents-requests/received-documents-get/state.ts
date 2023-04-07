import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { DocumentsRequestService } from '../request.service';

export const  receivedDocumentsGetRequestHandler = createRequestHandler('GetReceivedDocumentsRequest');

export function receivedDocumentsGetRequestReducer(state, action) {
  return receivedDocumentsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class ReceivedDocumentsGetRequestEffects {

  @Effect()
  receivedDocumentsGetData$: Observable<Action> = receivedDocumentsGetRequestHandler.effect(
    this.actions$,
    this.documentsRequestService.receivedDocumentsGetRequest.bind(this.documentsRequestService)
  );

  constructor(
    private actions$: Actions,
    private documentsRequestService: DocumentsRequestService
  ) {
  }
}
