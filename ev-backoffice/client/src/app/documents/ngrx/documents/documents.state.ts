import { createEntityAdapter, EntityAdapter, EntityState } from '@ngrx/entity';
import { createFeatureSelector } from '@ngrx/store';


import {
  DocumentAttachmentListState, DocumentPortalAccessState,
  RequiredApplicantDocumentModel,
  RequiredApplicantDocumentStateModel,
  RequiredDocument,
  DocumentAccessState,
} from '../../models/documents.model';
import { ApplicantSentDocuments } from '../../models/sent-document.model';
import { ApplicantReceivedDocuments } from '../../models/received-document.model';

export const DOCUMENTS_STATE = 'Documents';

export interface DocumentsState extends EntityState<RequiredDocument> {
  documentAccessState: DocumentAccessState;
  documentPortalAccessState: DocumentPortalAccessState;
  requireApplicantDocumentsState: RequiredApplicantDocumentStateModel[];
  requireApplicantDocuments: RequiredApplicantDocumentModel[];
  documentAttachmentListData: DocumentAttachmentListState;
  fileUploading: boolean;
  selectedDocumentAttachmentsMap: any;
  applicantSentDocuments: ApplicantSentDocuments[];
  applicantReceivedDocuments: ApplicantReceivedDocuments[];
  openedAllSentPanels: boolean;
  openedAllReceivedPanels: boolean;
  fileDownloading: boolean;

}

export const adapter: EntityAdapter<RequiredDocument> = createEntityAdapter<RequiredDocument>();

const documentsSelectors = adapter.getSelectors();


export const selectDocumentsState = createFeatureSelector<DocumentsState>(DOCUMENTS_STATE);

export const selectRequireDocumentEntities = documentsSelectors.selectEntities;

export const selectDocumentPortalAccessState = (state: DocumentsState) => state.documentPortalAccessState;

export const selectRequireDocumentItems = (state: DocumentsState) => state.requireApplicantDocumentsState;

export const selectRequireDocuments = (state: DocumentsState) => state.requireApplicantDocuments;

export const selectDocumentAttachmentsSelectedState = (state: DocumentsState) => state.selectedDocumentAttachmentsMap;

export const selectFileUploadingState = (state: DocumentsState) => state.fileUploading;

export const selectSentDocuments = (state: DocumentsState) => state.applicantSentDocuments;

export const selectReceivedDocuments = (state: DocumentsState) => state.applicantReceivedDocuments;

export const selectOpenAllSentPanelsState = (state: DocumentsState) => state.openedAllSentPanels;

export const selectOpenAllReceivedPanelsState = (state: DocumentsState) => state.openedAllReceivedPanels;

export const selectFileDownloadingState = (state: DocumentsState) => state.fileDownloading;

export const selectDocumentAccessState = (state: DocumentsState) => state.documentAccessState;

