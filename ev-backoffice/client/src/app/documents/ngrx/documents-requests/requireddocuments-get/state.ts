import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { DocumentsRequestService } from '../request.service';

export const  requiredDocumentsGetRequestHandler = createRequestHandler('GetRequiredDocumentsRequest');

export function requiredDocumentsGetRequestReducer(state, action) {
  return requiredDocumentsGetRequestHandler.reducer(state, action);
}

@Injectable()
export class RequiredDocumentsGetRequestEffects {

  @Effect()
  documentsGetData$: Observable<Action> = requiredDocumentsGetRequestHandler.effect(
    this.actions$,
    this.documentsRequestService.requiredDocumentsGetRequest.bind(this.documentsRequestService)
  );

  constructor(
    private actions$: Actions,
    private documentsRequestService: DocumentsRequestService
  ) {
  }
}
