import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect } from '@ngrx/effects';

import { Observable } from 'rxjs';

import { createRequestHandler } from '../../../../core/ngrx/utils';
import { DocumentsRequestService } from '../request.service';

export const  uploadDocumentAttachmentPostRequestHandler = createRequestHandler('PostDocumentAttachmentUploadRequest');

export function uploadDocumentAttachmentPostRequestReducer(state, action) {
  return uploadDocumentAttachmentPostRequestHandler.reducer(state, action);
}

@Injectable()
export class UploadDocumentAttachmentPostRequestEffects {

  @Effect()
  documentAttachmentPostData$: Observable<Action> = uploadDocumentAttachmentPostRequestHandler.effect(
    this.actions$,
    this.documentsRequestService.uploadDocumentAttachmentPostRequest.bind(this.documentsRequestService)
  );

  constructor(
    private actions$: Actions,
    private documentsRequestService: DocumentsRequestService
  ) {
  }
}
