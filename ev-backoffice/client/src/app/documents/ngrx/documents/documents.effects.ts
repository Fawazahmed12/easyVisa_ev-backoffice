import { Injectable } from '@angular/core';

import { Action } from '@ngrx/store';
import { Actions, Effect, ofType } from '@ngrx/effects';

import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

import {
  DocumentAttachmentListState,
  DocumentPortalAccessState,DocumentAccessState,
  RequiredApplicantDocumentModel
} from '../../models/documents.model';
import { RequestFailAction, RequestSuccessAction } from '../../../core/ngrx/utils';

import {
  DocumentsActionTypes,
  GetRequiredDocuments,
  GetRequiredDocumentsSuccess,
  GetRequiredDocumentsFailure,
  PostDocumentAttachmentUpload,
  PostDocumentAttachmentUploadSuccess,
  PostDocumentAttachmentUploadFailure,
  DeleteDocumentAttachments,
  DeleteDocumentAttachmentsSuccess,
  DeleteDocumentAttachmentsFailure,
  PostDocumentActionDate,
  PostDocumentActionDateSuccess,
  PostDocumentActionDateFailure,
  GetSentDocumentsFailure,
  GetSentDocuments,
  GetSentDocumentsSuccess,
  GetReceivedDocuments,
  GetReceivedDocumentsSuccess,
  GetReceivedDocumentsFailure,
  UpdateDocumentApproval,
  UpdateDocumentApprovalSuccess,
  UpdateDocumentApprovalFailure,
  GetDocumentPortalAccessStateSuccess,
  GetDocumentPortalAccessStateFailure,
  GetDocumentAccessState,
  GetDocumentAccessStateSuccess,
  GetDocumentAccessStateFailure,
} from './documents.actions';

import { documentPortalAccessGetRequestHandler } from '../documents-requests/documentportal-access-get/state';
import { requiredDocumentsGetRequestHandler } from '../documents-requests/requireddocuments-get/state';
import { uploadDocumentAttachmentPostRequestHandler } from '../documents-requests/document-attachment-post/state';
import { documentAttachmentsDeleteRequestHandler } from '../documents-requests/document-attachments-delete/state';
import { documentActionDatePostRequestHandler } from '../documents-requests/document-actiondate-post/state';
import { ApplicantSentDocuments } from '../../models/sent-document.model';
import { sentDocumentsGetRequestHandler } from '../documents-requests/sent-documents-get/state';
import { receivedDocumentsGetRequestHandler } from '../documents-requests/received-documents-get/state';
import { ApplicantReceivedDocuments } from '../../models/received-document.model';
import { documentApprovalPatchRequestHandler } from '../documents-requests/document-approval-patch/state';
import {documentAccessGetRequestHandler} from '../documents-requests/documentaccess-get/state';

@Injectable()
export class DocumentsEffects {

  @Effect()
  getDocumentPortalAccess$: Observable<Action> = this.actions$.pipe(
    ofType(DocumentsActionTypes.GetDocumentPortalAccess),
    map(({ payload }: GetRequiredDocuments) => documentPortalAccessGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getDocumentPortalAccessSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(documentPortalAccessGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<DocumentPortalAccessState>) => new GetDocumentPortalAccessStateSuccess(payload))
  );

  @Effect()
  getDocumentPortalAccessFailure$: Observable<Action> = this.actions$.pipe(
    ofType(documentPortalAccessGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetDocumentPortalAccessStateFailure(payload))
  );

  @Effect()
  getRequiredDocuments$: Observable<Action> = this.actions$.pipe(
    ofType(DocumentsActionTypes.GetRequiredDocuments),
    map(({ payload }: GetRequiredDocuments) => requiredDocumentsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getRequiredDocumentsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(requiredDocumentsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<RequiredApplicantDocumentModel[]>) => new GetRequiredDocumentsSuccess(payload))
  );

  @Effect()
  getRequiredDocumentsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(requiredDocumentsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetRequiredDocumentsFailure(payload))
  );

  @Effect()
  postDocumentAttachment$: Observable<Action> = this.actions$.pipe(
    ofType(DocumentsActionTypes.PostDocumentAttachmentUpload),
    map(({ payload }: PostDocumentAttachmentUpload) => uploadDocumentAttachmentPostRequestHandler.requestAction(payload))
  );

  @Effect()
  postDocumentAttachmentSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(uploadDocumentAttachmentPostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new PostDocumentAttachmentUploadSuccess(payload))
  );

  @Effect()
  postDocumentAttachmentFailure$: Observable<Action> = this.actions$.pipe(
    ofType(uploadDocumentAttachmentPostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new PostDocumentAttachmentUploadFailure(payload))
  );

  @Effect()
  deleteDocumentAttachments$: Observable<Action> = this.actions$.pipe(
    ofType(DocumentsActionTypes.DeleteDocumentAttachments),
    map(({ payload }: DeleteDocumentAttachments) => documentAttachmentsDeleteRequestHandler.requestAction(payload))
  );

  @Effect()
  deleteDocumentAttachmentsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(documentAttachmentsDeleteRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<DocumentAttachmentListState>) => new DeleteDocumentAttachmentsSuccess(payload))
  );

  @Effect()
  deleteDocumentAttachmentsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(documentAttachmentsDeleteRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new DeleteDocumentAttachmentsFailure(payload))
  );

  @Effect()
  getSentDocuments$: Observable<Action> = this.actions$.pipe(
    ofType(DocumentsActionTypes.GetSentDocuments),
    map(({ payload }: GetSentDocuments) => sentDocumentsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getSentDocumentsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(sentDocumentsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<ApplicantSentDocuments[]>) => new GetSentDocumentsSuccess(payload))
  );

  @Effect()
  getSentDocumentsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(sentDocumentsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetSentDocumentsFailure(payload))
  );

  @Effect()
  postDocumentActionDate$: Observable<Action> = this.actions$.pipe(
    ofType(DocumentsActionTypes.PostDocumentActionDate),
    map(({ payload }: PostDocumentActionDate) => documentActionDatePostRequestHandler.requestAction(payload))
  );

  @Effect()
  postDocumentActionDateSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(documentActionDatePostRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new PostDocumentActionDateSuccess(payload))
  );

  @Effect()
  postDocumentActionDateFailure$: Observable<Action> = this.actions$.pipe(
    ofType(documentActionDatePostRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new PostDocumentActionDateFailure(payload))
  );

  @Effect()
  getReceivedDocuments$: Observable<Action> = this.actions$.pipe(
    ofType(DocumentsActionTypes.GetReceivedDocuments),
    map(({ payload }: GetReceivedDocuments) => receivedDocumentsGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getReceivedDocumentsSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(receivedDocumentsGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<ApplicantReceivedDocuments[]>) => new GetReceivedDocumentsSuccess(payload))
  );

  @Effect()
  getReceivedDocumentsFailure$: Observable<Action> = this.actions$.pipe(
    ofType(receivedDocumentsGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new GetReceivedDocumentsFailure(payload))
  );

  @Effect()
  updateDocumentApproval$: Observable<Action> = this.actions$.pipe(
    ofType(DocumentsActionTypes.UpdateDocumentApproval),
    map(({ payload }: UpdateDocumentApproval) => documentApprovalPatchRequestHandler.requestAction(payload))
  );

  @Effect()
  updateDocumentApprovalSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(documentApprovalPatchRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map(({ payload }: RequestSuccessAction<any>) => new UpdateDocumentApprovalSuccess(payload))
  );

  @Effect()
  updateDocumentApprovalFailure$: Observable<Action> = this.actions$.pipe(
    ofType(documentApprovalPatchRequestHandler.ActionTypes.REQUEST_FAIL),
    map(({ payload }: RequestFailAction<any>) => new UpdateDocumentApprovalFailure(payload))
  );

  @Effect()
  getDocumentAccess$: Observable<Action> = this.actions$.pipe(
    ofType(DocumentsActionTypes.GetDocumentAccessState),
    map( ({ payload }: GetDocumentAccessState) => documentAccessGetRequestHandler.requestAction(payload))
  );

  @Effect()
  getDocumentAccessSuccess$: Observable<Action> = this.actions$.pipe(
    ofType(documentAccessGetRequestHandler.ActionTypes.REQUEST_SUCCESS),
    map( ({ payload }: RequestSuccessAction<DocumentAccessState>) => new GetDocumentAccessStateSuccess(payload))
  );

  @Effect()
  getDocumentAccessFailure$: Observable<Action> = this.actions$.pipe(
    ofType(documentAccessGetRequestHandler.ActionTypes.REQUEST_FAIL),
    map( ({ payload }: RequestSuccessAction<DocumentAccessState>) => new GetDocumentAccessStateFailure(payload))
  );

  constructor(private actions$: Actions) {
  }
}
