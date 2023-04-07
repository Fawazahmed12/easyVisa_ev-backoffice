import {
  DocumentAccessState,
  DocumentAttachmentListState,
  DocumentPortalAccessState,
  RequiredApplicantDocumentModel
} from '../../models/documents.model';
import { RequestState } from '../../../core/ngrx/utils';
import { ApplicantSentDocuments } from '../../models/sent-document.model';
import { ApplicantReceivedDocuments } from '../../models/received-document.model';

export const DOCUMENTS_REQUEST = 'DocumentsRequests';

export interface DocumentsRequestState {
  documentPortalAccessGet?: RequestState<DocumentPortalAccessState>;
  requiredDocumentsGet?: RequestState<RequiredApplicantDocumentModel[]>;
  documentAttachmentPost?: RequestState<any>;
  documentAttachmentsDelete?: RequestState<DocumentAttachmentListState>;
  sentDocumentsGet?: RequestState<ApplicantSentDocuments[]>;
  documentActionDatePost?: RequestState<any>;
  receivedDocumentsGet?: RequestState<ApplicantReceivedDocuments[]>;
  documentApprovalPatch?: RequestState<any>;
  documentAccessGet?: RequestState<DocumentAccessState>;
}

export const selectDocumentsRequestState = (state) => state[ DOCUMENTS_REQUEST ];

export const selectDocumentPortalAccessGetState = (state: DocumentsRequestState) => state.documentPortalAccessGet;
export const selectRequiredDocumentsGetState = (state: DocumentsRequestState) => state.requiredDocumentsGet;
export const selectDocumentAttachmentPostState = (state: DocumentsRequestState) => state.documentAttachmentPost;
export const selectDocumentAttachmentsDeleteState = (state: DocumentsRequestState) => state.documentAttachmentsDelete;
export const selectApplicantSentDocumentsGetState = (state: DocumentsRequestState) => state.sentDocumentsGet;
export const selectDocumentActionDatePostState = (state: DocumentsRequestState) => state.documentActionDatePost;
export const selectApplicantReceivedDocumentsGetState = (state: DocumentsRequestState) => state.receivedDocumentsGet;
export const selectDocumentApprovalPatchState = (state: DocumentsRequestState) => state.documentApprovalPatch;
export const selectDocumentAccessGetState = (state: DocumentsRequestState) => state.documentAccessGet;

export { documentPortalAccessGetRequestHandler } from './documentportal-access-get/state';
export { requiredDocumentsGetRequestHandler } from './requireddocuments-get/state';
export { uploadDocumentAttachmentPostRequestHandler } from './document-attachment-post/state';
export { documentAttachmentsDeleteRequestHandler } from './document-attachments-delete/state';
export { documentActionDatePostRequestHandler } from './document-actiondate-post/state';
export { sentDocumentsGetRequestHandler } from './sent-documents-get/state';
export { receivedDocumentsGetRequestHandler } from './received-documents-get/state';
export { documentApprovalPatchRequestHandler } from './document-approval-patch/state';
export {documentAccessGetRequestHandler} from './documentaccess-get/state';

