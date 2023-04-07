import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { DocumentsRequestService } from '../request.service';

export const documentApprovalPatchRequestHandler = createRequestHandler('PatchDocumentApprovalRequest');

export function documentApprovalPatchRequestReducer(state, action) {
  return documentApprovalPatchRequestHandler.reducer(state, action);
}

@Injectable()
export class DocumentApprovalPatchRequestEffects {

  @Effect()
  documentApprovalPatchData$: Observable<Action> = documentApprovalPatchRequestHandler.effect(
    this.actions$,
    this.documentsRequestService.documentApprovalPatchRequest.bind(this.documentsRequestService)
  );

  constructor(
    private actions$: Actions,
    private documentsRequestService: DocumentsRequestService
  ) {
  }
}
