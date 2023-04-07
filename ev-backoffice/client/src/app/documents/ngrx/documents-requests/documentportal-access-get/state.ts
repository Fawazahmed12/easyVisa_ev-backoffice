import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { DocumentsRequestService } from '../request.service';

export const  documentPortalAccessGetRequestHandler = createRequestHandler('GetDocumentPortalAccessRequest');

export function documentPortalAccessGetRequestReducer(state, action) {
  return documentPortalAccessGetRequestHandler.reducer(state, action);
}

@Injectable()
export class DocumentPortalAccessGetRequestEffects {

  @Effect()
  documentsGetData$: Observable<Action> = documentPortalAccessGetRequestHandler.effect(
    this.actions$,
    this.documentsRequestService.documentPortalAccessGetRequest.bind(this.documentsRequestService)
  );

  constructor(
    private actions$: Actions,
    private documentsRequestService: DocumentsRequestService
  ) {
  }
}
