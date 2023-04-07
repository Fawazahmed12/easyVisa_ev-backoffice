import { RequiredDocumentsGetRequestEffects } from './requireddocuments-get/state';
import { UploadDocumentAttachmentPostRequestEffects } from './document-attachment-post/state';
import { DocumentAttachmentsDeleteRequestEffects } from './document-attachments-delete/state';
import { SentDocumentsGetRequestEffects } from './sent-documents-get/state';
import { DocumentActionDatePostRequestEffects } from './document-actiondate-post/state';
import { ReceivedDocumentsGetRequestEffects } from './received-documents-get/state';
import { DocumentApprovalPatchRequestEffects } from './document-approval-patch/state';
import { DocumentPortalAccessGetRequestEffects } from './documentportal-access-get/state';
import { DocumentAccessGetRequestEffects } from './documentaccess-get/state';

export const DocumentsRequestEffects = [
  DocumentPortalAccessGetRequestEffects,
  RequiredDocumentsGetRequestEffects,
  UploadDocumentAttachmentPostRequestEffects,
  DocumentAttachmentsDeleteRequestEffects,
  SentDocumentsGetRequestEffects,
  DocumentActionDatePostRequestEffects,
  ReceivedDocumentsGetRequestEffects,
  DocumentApprovalPatchRequestEffects,
  DocumentAccessGetRequestEffects
];
