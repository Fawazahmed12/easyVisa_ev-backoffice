import { Action } from '@ngrx/store';

import {
  DocumentAccessState,
  DocumentAttachmentListState, DocumentPortalAccessState,
  RequiredApplicantDocumentModel
} from '../../models/documents.model';

import { DOCUMENTS_STATE } from './documents.state';
import { ApplicantSentDocuments } from '../../models/sent-document.model';
import { ApplicantReceivedDocuments } from '../../models/received-document.model';
import { DocumentsAccessResolverService } from '../../resolvers/documents-access-resolver.service';

export const DocumentsActionTypes = {
  GetDocumentPortalAccess: `[${DOCUMENTS_STATE}] DocumentPortal Access Request`,
  GetDocumentPortalAccessSuccess: `[${DOCUMENTS_STATE}] DocumentPortal Access Success`,
  GetDocumentPortalAccessFailure: `[${DOCUMENTS_STATE}] DocumentPortal Access Failure`,
  GetRequiredDocuments: `[${DOCUMENTS_STATE}] Required Documents Request`,
  GetRequiredDocumentsSuccess: `[${DOCUMENTS_STATE}] Required Documents Success`,
  GetRequiredDocumentsFailure: `[${DOCUMENTS_STATE}] Required Documents Failure`,
  PostDocumentAttachmentUpload: `[${DOCUMENTS_STATE}] Document Attachment Upload Request`,
  PostDocumentAttachmentUploadSuccess: `[${DOCUMENTS_STATE}] Document Attachment Upload Success`,
  PostDocumentAttachmentUploadFailure: `[${DOCUMENTS_STATE}] Document Attachment Upload Failure`,
  DeleteDocumentAttachments: `[${DOCUMENTS_STATE}] Delete Document Attachments Request`,
  DeleteDocumentAttachmentsSuccess: `[${DOCUMENTS_STATE}] Delete Document Attachments Success`,
  DeleteDocumentAttachmentsFailure: `[${DOCUMENTS_STATE}] Delete Document Attachments Failure`,
  SelectDocumentAttachment: `[${DOCUMENTS_STATE}] Select Document Attachment`,
  ResetSelectedDocumentAttachments: `[${DOCUMENTS_STATE}] Reset Selected Document Attachments`,
  GetSentDocuments: `[${DOCUMENTS_STATE}] Sent Documents Request`,
  GetSentDocumentsSuccess: `[${DOCUMENTS_STATE}] Sent Documents Success`,
  GetSentDocumentsFailure: `[${DOCUMENTS_STATE}] Sent Documents Failure`,
  PostDocumentActionDate: `[${DOCUMENTS_STATE}] Post Document Action Date Request`,
  PostDocumentActionDateSuccess: `[${DOCUMENTS_STATE}] Post Document Action Date Success`,
  PostDocumentActionDateFailure: `[${DOCUMENTS_STATE}] Post Document Action Date Failure`,
  GetReceivedDocuments: `[${DOCUMENTS_STATE}] Received Documents Request`,
  GetReceivedDocumentsSuccess: `[${DOCUMENTS_STATE}] Received Documents Success`,
  GetReceivedDocumentsFailure: `[${DOCUMENTS_STATE}] Received Documents Failure`,
  UpdateDocumentApproval: `[${DOCUMENTS_STATE}] Update Document Approval`,
  UpdateDocumentApprovalSuccess: `[${DOCUMENTS_STATE}] Update Document Approval Success`,
  UpdateDocumentApprovalFailure: `[${DOCUMENTS_STATE}] Update Document Approval Failure`,
  OpenAllSentPanels: `[${DOCUMENTS_STATE}] Open All Sent Panels`,
  OpenAllReceivedPanels: `[${DOCUMENTS_STATE}] Open All Received Panels`,
  FileDownloading: `[${DOCUMENTS_STATE}] File Downloading`,
  GetDocumentAccessState:`[${DOCUMENTS_STATE}] Document Access Request`,
  GetDocumentAccessStateSuccess:`[${DOCUMENTS_STATE}] Document Access Success`,
  GetDocumentAccessStateFailure:`[${DOCUMENTS_STATE}] Document Access Failure`,
};


export class GetDocumentPortalAccessState implements Action {
  readonly type = DocumentsActionTypes.GetDocumentPortalAccess;

  constructor(public payload: string) {
  }
}


export class GetDocumentPortalAccessStateSuccess implements Action {
  readonly type = DocumentsActionTypes.GetDocumentPortalAccessSuccess;

  constructor(public payload: DocumentPortalAccessState) {
  }
}

export class GetDocumentPortalAccessStateFailure implements Action {
  readonly type = DocumentsActionTypes.GetDocumentPortalAccessFailure;

  constructor(public payload: any) {
  }
}


export class GetRequiredDocuments implements Action {
  readonly type = DocumentsActionTypes.GetRequiredDocuments;

  constructor(public payload: string) {
  }
}

export class GetRequiredDocumentsSuccess implements Action {
  readonly type = DocumentsActionTypes.GetRequiredDocumentsSuccess;

  constructor(public payload: RequiredApplicantDocumentModel[]) {
  }
}

export class GetRequiredDocumentsFailure implements Action {
  readonly type = DocumentsActionTypes.GetRequiredDocumentsFailure;

  constructor(public payload: any) {
  }
}

export class PostDocumentAttachmentUpload implements Action {
  readonly type = DocumentsActionTypes.PostDocumentAttachmentUpload;

  constructor(public payload: string) {
  }
}

export class PostDocumentAttachmentUploadSuccess implements Action {
  readonly type = DocumentsActionTypes.PostDocumentAttachmentUploadSuccess;

  constructor(public payload: any) {
  }
}

export class PostDocumentAttachmentUploadFailure implements Action {
  readonly type = DocumentsActionTypes.PostDocumentAttachmentUploadFailure;

  constructor(public payload: any) {
  }
}

export class DeleteDocumentAttachments implements Action {
  readonly type = DocumentsActionTypes.DeleteDocumentAttachments;

  constructor(public payload: string) {
  }
}

export class DeleteDocumentAttachmentsSuccess implements Action {
  readonly type = DocumentsActionTypes.DeleteDocumentAttachmentsSuccess;

  constructor(public payload: DocumentAttachmentListState) {
  }
}

export class DeleteDocumentAttachmentsFailure implements Action {
  readonly type = DocumentsActionTypes.DeleteDocumentAttachmentsFailure;

  constructor(public payload: any) {
  }
}


export class SelectDocumentAttachment implements Action {
  readonly type = DocumentsActionTypes.SelectDocumentAttachment;

  constructor(public payload: any) {
  }
}

export class ResetSelectedDocumentAttachments implements Action {
  readonly type = DocumentsActionTypes.ResetSelectedDocumentAttachments;

  constructor(public payload: any) {
  }
}

export class GetSentDocuments implements Action {
  readonly type = DocumentsActionTypes.GetSentDocuments;

  constructor(public payload: string) {
  }
}

export class GetSentDocumentsSuccess implements Action {
  readonly type = DocumentsActionTypes.GetSentDocumentsSuccess;

  constructor(public payload: ApplicantSentDocuments[]) {
  }
}

export class GetSentDocumentsFailure implements Action {
  readonly type = DocumentsActionTypes.GetSentDocumentsFailure;

  constructor(public payload: any) {
  }
}

export class PostDocumentActionDate implements Action {
  readonly type = DocumentsActionTypes.PostDocumentActionDate;

  constructor(public payload: string) {
  }
}

export class PostDocumentActionDateSuccess implements Action {
  readonly type = DocumentsActionTypes.PostDocumentActionDateSuccess;

  constructor(public payload: any) {
  }
}

export class PostDocumentActionDateFailure implements Action {
  readonly type = DocumentsActionTypes.PostDocumentActionDateFailure;

  constructor(public payload: any) {
  }
}


export class GetReceivedDocuments implements Action {
  readonly type = DocumentsActionTypes.GetReceivedDocuments;

  constructor(public payload: string) {
  }
}

export class GetReceivedDocumentsSuccess implements Action {
  readonly type = DocumentsActionTypes.GetReceivedDocumentsSuccess;

  constructor(public payload: ApplicantReceivedDocuments[]) {
  }
}

export class GetReceivedDocumentsFailure implements Action {
  readonly type = DocumentsActionTypes.GetReceivedDocumentsFailure;

  constructor(public payload: any) {
  }
}

export class UpdateDocumentApproval implements Action {
  readonly type = DocumentsActionTypes.UpdateDocumentApproval;

  constructor(public payload: string) {
  }
}

export class UpdateDocumentApprovalSuccess implements Action {
  readonly type = DocumentsActionTypes.UpdateDocumentApprovalSuccess;

  constructor(public payload: any) {
  }
}

export class UpdateDocumentApprovalFailure implements Action {
  readonly type = DocumentsActionTypes.UpdateDocumentApprovalFailure;

  constructor(public payload: any) {
  }
}


export class OpenAllSentPanels implements Action {
  readonly type = DocumentsActionTypes.OpenAllSentPanels;

  constructor(public payload: any) {
  }
}


export class OpenAllReceivedPanels implements Action {
  readonly type = DocumentsActionTypes.OpenAllReceivedPanels;

  constructor(public payload: any) {
  }
}



export class FileDownloading implements Action {
  readonly type = DocumentsActionTypes.FileDownloading;

  constructor(public payload: any) {
  }
}

export class GetDocumentAccessState implements Action{
  readonly type = DocumentsActionTypes.GetDocumentAccessState;
  constructor(public payload: string) {
  }
}

export class GetDocumentAccessStateSuccess implements Action{
  readonly type = DocumentsActionTypes.GetDocumentAccessStateSuccess;
  constructor(public payload: DocumentAccessState) {
  }
}

export class GetDocumentAccessStateFailure implements Action{
  readonly type = DocumentsActionTypes.GetDocumentAccessStateFailure;
  constructor(public payload: any) {
  }
}



export type DocumentsActionsUnion =
  | GetRequiredDocuments
  | GetRequiredDocumentsSuccess
  | GetRequiredDocumentsFailure
  | PostDocumentAttachmentUpload
  | PostDocumentAttachmentUploadSuccess
  | PostDocumentAttachmentUploadFailure
  | DeleteDocumentAttachments
  | DeleteDocumentAttachmentsSuccess
  | DeleteDocumentAttachmentsFailure
  | SelectDocumentAttachment
  | ResetSelectedDocumentAttachments
  | GetSentDocuments
  | GetSentDocumentsSuccess
  | GetSentDocumentsFailure
  | PostDocumentActionDate
  | PostDocumentActionDateSuccess
  | PostDocumentActionDateFailure
  | GetReceivedDocuments
  | GetReceivedDocumentsSuccess
  | GetReceivedDocumentsFailure
  | UpdateDocumentApproval
  | UpdateDocumentApprovalSuccess
  | UpdateDocumentApprovalFailure
  | OpenAllSentPanels
  | OpenAllReceivedPanels
  | FileDownloading
  | GetDocumentAccessState
  | GetDocumentAccessStateSuccess
  | GetDocumentAccessStateFailure;
