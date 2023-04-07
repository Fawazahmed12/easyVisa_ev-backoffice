import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { DocumentsRequestService } from '../request.service';

export const  documentActionDatePostRequestHandler = createRequestHandler('PostDocumentActionDateRequest');

export function documentActionDatePostRequestReducer(state, action) {
  return documentActionDatePostRequestHandler.reducer(state, action);
}

@Injectable()
export class DocumentActionDatePostRequestEffects {

  @Effect()
  documentActionDatePostData$: Observable<Action> = documentActionDatePostRequestHandler.effect(
    this.actions$,
    this.documentsRequestService.documentActionDatePostRequest.bind(this.documentsRequestService)
  );

  constructor(
    private actions$: Actions,
    private documentsRequestService: DocumentsRequestService
  ) {
  }
}
