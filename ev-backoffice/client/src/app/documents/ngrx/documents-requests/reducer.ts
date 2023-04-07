import { DocumentsRequestState } from './state';
import { requiredDocumentsGetRequestReducer } from './requireddocuments-get/state';
import { uploadDocumentAttachmentPostRequestReducer } from './document-attachment-post/state';
import { documentAttachmentsDeleteRequestReducer } from './document-attachments-delete/state';
import { sentDocumentsGetRequestReducer } from './sent-documents-get/state';
import { receivedDocumentsGetRequestReducer } from './received-documents-get/state';
import { documentApprovalPatchRequestReducer } from './document-approval-patch/state';
import { documentPortalAccessGetRequestReducer } from './documentportal-access-get/state';
import { documentAccessGetRequestReducer } from './documentaccess-get/state';

export function reducer(state: DocumentsRequestState = {}, action) {
  return {
    documentPortalAccessGet: documentPortalAccessGetRequestReducer(state.documentPortalAccessGet, action),
    requiredDocumentsGet: requiredDocumentsGetRequestReducer(state.requiredDocumentsGet, action),
    documentAttachmentPost: uploadDocumentAttachmentPostRequestReducer(state.documentAttachmentPost, action),
    documentAttachmentsDelete: documentAttachmentsDeleteRequestReducer(state.documentAttachmentsDelete, action),
    sentDocumentsGet: sentDocumentsGetRequestReducer(state.sentDocumentsGet, action),
    documentActionDatePost: documentAttachmentsDeleteRequestReducer(state.documentActionDatePost, action),
    receivedDocumentsGet: receivedDocumentsGetRequestReducer(state.receivedDocumentsGet, action),
    documentApprovalPatch: documentApprovalPatchRequestReducer(state.documentApprovalPatch, action),
    documentAccessGet: documentAccessGetRequestReducer(state.documentAccessGet, action),
  };
}
