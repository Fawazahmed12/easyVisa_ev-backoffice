import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { DocumentsRequestService } from '../request.service';

export const  documentAttachmentsDeleteRequestHandler = createRequestHandler('DeleteDocumentAttachmentsRequest');

export function documentAttachmentsDeleteRequestReducer(state, action) {
  return documentAttachmentsDeleteRequestHandler.reducer(state, action);
}

@Injectable()
export class DocumentAttachmentsDeleteRequestEffects {

  @Effect()
  documentAttachmentsDeleteData$: Observable<Action> = documentAttachmentsDeleteRequestHandler.effect(
    this.actions$,
    this.documentsRequestService.documentAttachmentsDeleteRequest.bind(this.documentsRequestService)
  );

  constructor(
    private actions$: Actions,
    private documentsRequestService: DocumentsRequestService
  ) {
  }
}
