import { Injectable } from '@angular/core';

import { Action, select, Store } from '@ngrx/store';

import { Observable } from 'rxjs';
import { filter, share } from 'rxjs/operators';

import { find, isEqual } from 'lodash-es';

import { State } from '../../core/ngrx/state';
import { RequestState } from '../../core/ngrx/utils';
import { throwIfRequestFailError } from '../../core/ngrx/utils/rxjs-utils';
import {
  DeleteDocumentAttachments, FileDownloading,
  GetDocumentPortalAccessState,
  GetReceivedDocuments,
  GetRequiredDocuments,
  GetSentDocuments, OpenAllReceivedPanels, OpenAllSentPanels,
  PostDocumentActionDate,
  PostDocumentAttachmentUpload,
  ResetSelectedDocumentAttachments,
  SelectDocumentAttachment,
  UpdateDocumentApproval,
  GetDocumentAccessState,
} from '../ngrx/documents/documents.actions';
import {
  deleteDocumentAttachmentsRequestState,
  getDocumentAttachmentsSelectedState,
  getDocumentPortalAccessRequestState,
  getDocumentPortalAccessState, getFileDownloadingState,
  getFileUploadingState, getOpenAllReceivedPanelsState, getOpenAllSentPanelsState,
  getReceivedDocumentRequestState,
  getReceivedDocuments,
  getRequiredDocumentRequestState,
  getRequiredDocuments,
  getSentDocumentRequestState,
  getSentDocuments,
  patchDocumentApprovalRequestState,
  postDocumentActionDateRequestState,
  postRequiredDocRequestState,
  getDocumentAccessState,
  getDocumentAccessRequestState
} from '../ngrx/state';
import {
  DocumentAccessState,
  DocumentPortalAccessState,
  RequiredApplicantDocumentModel
} from '../models/documents.model';
import { ApplicantSentDocuments } from '../models/sent-document.model';
import { ApplicantReceivedDocuments } from '../models/received-document.model';
import { HttpErrorResponse } from '@angular/common/http';
import { ModalService } from '../../core/services';
import { DocumentsEffects } from '../ngrx/documents/documents.effects';
import {QuestionnaireAccessState} from '../../questionnaire/models/questionnaire.model';

@Injectable()
export class DocumentsService {
  documentPortalAccessGetRequest$: Observable<RequestState<DocumentPortalAccessState>>;
  requiredDocumentsGetRequest$: Observable<RequestState<any>>;
  requiredApplicantDocuments$: Observable<RequiredApplicantDocumentModel[]>;
  documentAttachmentPostRequest$: Observable<any>;
  fileUploading$: Observable<boolean>;
  documentAttachmentsDeleteRequest$: Observable<RequestState<any>>;
  documentAttachmentsSelection$: Observable<RequestState<any>>;
  openAllSentPanels$: Observable<boolean>;
  openAllReceivedPanels$: Observable<boolean>;
  sentDocumentsGetRequest$: Observable<RequestState<any>>;
  applicantSentDocuments$: Observable<ApplicantSentDocuments[]>;
  documentActionDatePostRequest$: Observable<any>;
  receivedDocumentsGetRequest$: Observable<RequestState<any>>;
  applicantReceivedDocuments$: Observable<ApplicantReceivedDocuments[]>;
  documentApprovalPatchRequest$: Observable<any>;
  documentPortalAccessData$: Observable<DocumentPortalAccessState>;
  documentAttachmentPostFailAction$: Observable<Action>;
  documentAttachmentsDeleteFailAction$: Observable<Action>;
  documentApprovalPatchFailAction$: Observable<Action>;
  documentActionDatePostFailAction$: Observable<Action>;
  fileDownloading$: Observable<boolean>;
  documentAccessData$: Observable<DocumentAccessState>;
  documentAccessGetRequest$: Observable<RequestState<DocumentAccessState>>;

  constructor(private store: Store<State>,
              private documentsEffects: DocumentsEffects,
              private modalService: ModalService) {
    this.documentPortalAccessGetRequest$ = this.store.pipe(select(getDocumentPortalAccessRequestState));
    this.requiredDocumentsGetRequest$ = this.store.pipe(select(getRequiredDocumentRequestState));
    this.requiredApplicantDocuments$ = this.store.pipe(select(getRequiredDocuments));
    this.documentAttachmentPostRequest$ = this.store.pipe(select(postRequiredDocRequestState));
    this.fileUploading$ = this.store.pipe(select(getFileUploadingState));
    this.documentAttachmentsDeleteRequest$ = this.store.pipe(select(deleteDocumentAttachmentsRequestState));
    this.documentAttachmentsSelection$ = this.store.pipe(select(getDocumentAttachmentsSelectedState));
    this.openAllSentPanels$ = this.store.pipe(select(getOpenAllSentPanelsState));
    this.openAllReceivedPanels$ = this.store.pipe(select(getOpenAllReceivedPanelsState));
    this.sentDocumentsGetRequest$ = this.store.pipe(select(getSentDocumentRequestState));
    this.applicantSentDocuments$ = this.store.pipe(select(getSentDocuments));
    this.documentActionDatePostRequest$ = this.store.pipe(select(postDocumentActionDateRequestState));
    this.receivedDocumentsGetRequest$ = this.store.pipe(select(getReceivedDocumentRequestState));
    this.applicantReceivedDocuments$ = this.store.pipe(select(getReceivedDocuments));
    this.documentApprovalPatchRequest$ = this.store.pipe(select(patchDocumentApprovalRequestState));
    this.documentPortalAccessData$ = this.store.pipe(select(getDocumentPortalAccessState));
    this.documentAttachmentPostFailAction$ = this.documentsEffects.postDocumentAttachmentFailure$;
    this.documentAttachmentsDeleteFailAction$ = this.documentsEffects.deleteDocumentAttachmentsFailure$;
    this.documentApprovalPatchFailAction$ = this.documentsEffects.updateDocumentApprovalFailure$;
    this.documentActionDatePostFailAction$ = this.documentsEffects.postDocumentActionDateFailure$;
    this.fileDownloading$ = this.store.pipe(select(getFileDownloadingState));
    this.documentAccessData$ = this.store.pipe(select(getDocumentAccessState));
    this.documentAccessGetRequest$ = this.store.pipe(select(getDocumentAccessRequestState));
  }

  getDocumentPortalAccessRequest(activePackageId) {
    this.store.dispatch(new GetDocumentPortalAccessState(activePackageId));
    return this.documentPortalAccessGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }


  getApplicantRequiredDocuments(activePackageId) {
    this.store.dispatch(new GetRequiredDocuments(activePackageId));
    return this.requiredDocumentsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  uploadDocumentFile(data) {
    this.store.dispatch(new PostDocumentAttachmentUpload(data));
    return this.documentAttachmentPostRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  deleteDocumentAttachmentList(data) {
    this.store.dispatch(new DeleteDocumentAttachments(data));
    return this.documentAttachmentsDeleteRequest$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  selectDocumentAttachment(data) {
    this.store.dispatch(new SelectDocumentAttachment(data));
  }

  resetSelectedDocumentAttachments(data) {
    this.store.dispatch(new ResetSelectedDocumentAttachments(data));
  }

  openAllSentPanels(data) {
    this.store.dispatch(new OpenAllSentPanels(data));
  }

  openAllReceivedPanels(data) {
    this.store.dispatch(new OpenAllReceivedPanels(data));
  }

  downloadingFiles(data) {
    this.store.dispatch(new FileDownloading(data));
  }

  getApplicantSentDocuments(activePackageId) {
    this.store.dispatch(new GetSentDocuments(activePackageId));
    return this.sentDocumentsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  saveDocumentActionDate(data) {
    this.store.dispatch(new PostDocumentActionDate(data));
    return this.documentActionDatePostRequest$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  getApplicantReceivedDocuments(activePackageId) {
    this.store.dispatch(new GetReceivedDocuments(activePackageId));
    return this.receivedDocumentsGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }

  approvedEntireDocumentPanel(data) {
    this.store.dispatch(new UpdateDocumentApproval(data));
    return this.documentApprovalPatchRequest$.pipe(
      filter(response => response.loaded),
      throwIfRequestFailError(),
      share(),
    );
  }

  documentAccessErrorFilter(action, attachmentRefId = null) {
    const ACCESS_ERROR_TYPE = 'INVALID_DOCUMENTPORTAL_ACCESS';
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    const errors = payload.error.errors || [ payload.error ];
    const accessError = errors[ 0 ] || { type: '' };
    if (accessError.type !== ACCESS_ERROR_TYPE) {
      return false;
    }
    const errorMessages = accessError.message.split('|');
    const errorMessage = errorMessages[ 0 ];
    const sourceFieldId = errorMessages[ 1 ];
    if (sourceFieldId !== attachmentRefId) {
      return false;
    }
    accessError.message = errorMessage;
    return false;
  }

  documentAccessErrorHandler(action) {
    const payload: HttpErrorResponse = action.payload as HttpErrorResponse;
    this.modalService.showErrorModal(payload.error.errors || [ payload.error ]);
  }

  getDocumentAccessRequest(activePackageId) {
    this.store.dispatch(new GetDocumentAccessState(activePackageId));
    return this.documentAccessGetRequest$.pipe(
      filter(response => !response.loading),
      throwIfRequestFailError(),
      share(),
    );
  }
}
