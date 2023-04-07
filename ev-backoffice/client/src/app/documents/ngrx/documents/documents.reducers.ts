import { adapter, DocumentsState } from './documents.state';
import { DocumentsActionsUnion, DocumentsActionTypes, GetDocumentPortalAccessStateSuccess } from './documents.actions';
import {
  DocumentAttachmentListState,
  DocumentPanelType,
  RequiredApplicantDocumentModel,
  RequiredApplicantDocumentStateModel,
  RequiredDocument
} from '../../models/documents.model';

import { flatten, uniq, cloneDeep } from 'lodash-es';
import { SentDocument } from '../../models/sent-document.model';
import { ReceivedDocument } from '../../models/received-document.model';
import { PackagesActionTypes } from '../../../core/ngrx/packages/packages.actions';

export const initialState: DocumentsState = adapter.getInitialState({
  documentPortalAccessState: null,
  requireApplicantDocuments: null,
  requireApplicantDocumentsState: null,
  documentAttachmentListData: null,
  fileUploading: false,
  selectedDocumentAttachmentsMap: {},
  applicantSentDocuments: null,
  applicantReceivedDocuments: null,
  openedAllSentPanels: false,
  openedAllReceivedPanels: false,
  fileDownloading: false,
  documentAccessState: null
});


export function reducer(state = initialState, action: DocumentsActionsUnion) {
  switch (action.type) {
    case DocumentsActionTypes.GetDocumentPortalAccessSuccess: {
      const documentPortalAccessState = action.payload;
      return {
        ...state,
        documentPortalAccessState: {...documentPortalAccessState}
      };
    }
    case DocumentsActionTypes.GetDocumentAccessStateSuccess: {
      const documentAccessState = action.payload;
      return {
        ...state,
        documentAccessState: {...documentAccessState}
      };
    }
    case DocumentsActionTypes.GetRequiredDocumentsSuccess: {
      const applicantRequiredDocumentData = buildApplicantRequiredDocumentData(action.payload);
      return {
        ...adapter.setAll(applicantRequiredDocumentData.requiredDocuments, state),
        requireApplicantDocuments: applicantRequiredDocumentData.requireApplicantDocuments,
        requireApplicantDocumentsState: applicantRequiredDocumentData.requireApplicantDocumentsState
      };
    }
    case DocumentsActionTypes.PostDocumentAttachmentUpload: {
      return {
        ...state,
        fileUploading: true
      };
    }
    case DocumentsActionTypes.PostDocumentAttachmentUploadSuccess:
    case DocumentsActionTypes.DeleteDocumentAttachmentsSuccess: {
      const documentAttachmentListData = buildDocumentAttachmentListData(state, action.payload);
      return {
        ...documentAttachmentListData,
        fileUploading: false
      };
    }
    case DocumentsActionTypes.PostDocumentAttachmentUploadFailure: {
      return {
        ...state,
        fileUploading: false
      };
    }
    case DocumentsActionTypes.SelectDocumentAttachment: {
      return {
        ...state,
        selectedDocumentAttachmentsMap: buildDocumentAttachmentSelection(action, state)
      };
    }
    case DocumentsActionTypes.ResetSelectedDocumentAttachments: {
      return {
        ...state,
        selectedDocumentAttachmentsMap: {}
      };
    }
    case DocumentsActionTypes.GetSentDocumentsSuccess: {
      return {
        ...state,
        applicantSentDocuments: action.payload
      };
    }
    case DocumentsActionTypes.PostDocumentActionDateSuccess: {
      const documentActionDateData = buildDocumentActionDateData(state, action.payload);
      return {
        ...documentActionDateData
      };
    }
    case DocumentsActionTypes.GetReceivedDocumentsSuccess: {
      return {
        ...state,
        applicantReceivedDocuments: [action.payload]
      };
    }
    case DocumentsActionTypes.UpdateDocumentApprovalSuccess: {
      const documentApprovalData = buildDocumentApprovalData(state, action.payload);
      return {
        ...documentApprovalData
      };
    }
    case PackagesActionTypes.ClearActivePackage: {
      return{
        ...state,
        documentPortalAccessState: null,
        requireApplicantDocuments: null,
        requireApplicantDocumentsState: null,
        documentAttachmentListData: null,
        fileUploading: false,
        selectedDocumentAttachmentsMap: {},
        applicantSentDocuments: null,
        applicantReceivedDocuments: null,
      };
    }
    case DocumentsActionTypes.OpenAllSentPanels: {
      return {
        ...state,
        openedAllSentPanels: action.payload
      };
    }
    case DocumentsActionTypes.OpenAllReceivedPanels: {
      return {
        ...state,
        openedAllReceivedPanels: action.payload
      };
    }
    case DocumentsActionTypes.FileDownloading: {
      return {
        ...state,
        fileDownloading: action.payload
      };
    }
    default: {
      return state;
    }
  }
}


function buildApplicantRequiredDocumentData(requireApplicantDocuments: RequiredApplicantDocumentModel[]) {
  const requiredDocuments: RequiredDocument[] = uniq(flatten(requireApplicantDocuments.map((item) => item.requiredDocuments)));
  const requireApplicantDocumentsState: RequiredApplicantDocumentStateModel[] =
    requireApplicantDocuments.map((item) => ({
        ...item,
        requiredDocuments: item.requiredDocuments.map((requiredDocument) => requiredDocument.id)
      })
    );
  return {
    requiredDocuments,
    requireApplicantDocumentsState,
    requireApplicantDocuments
  };
}

function buildDocumentAttachmentListData(state, documentAttachmentListData: DocumentAttachmentListState) {
  const newState = cloneDeep(state);
  switch (documentAttachmentListData.documentType) {
    case DocumentPanelType.REQUIRED_DOCUMENT: {
      const requiredApplicantDocuments = newState.requireApplicantDocuments;
      const requiredDocumentApplicantData = requiredApplicantDocuments.find((requireApplicantDocument) => requireApplicantDocument.applicantId === documentAttachmentListData.applicantId);
      const requiredDocument: RequiredDocument = requiredDocumentApplicantData.requiredDocuments.find((requiredDoc) => requiredDoc.id === documentAttachmentListData.id);
      requiredDocument.attachments = [...documentAttachmentListData.attachments];
      requiredDocument.isApproved = documentAttachmentListData.isApproved;
      break;
    }
    case DocumentPanelType.DOCUMENT_SENT_TO_US: {
      const applicantSentDocuments = newState.applicantSentDocuments;
      const sentDocumentApplicantData = applicantSentDocuments.find((applicantSentDocument) => applicantSentDocument.applicantId === documentAttachmentListData.applicantId);
      const sentDocument: SentDocument = sentDocumentApplicantData.sentDocuments.find((sentDoc) => sentDoc.id === documentAttachmentListData.id);
      sentDocument.attachments = [ ...documentAttachmentListData.attachments ];
      sentDocument.isApproved = documentAttachmentListData.isApproved;
      break;
    }
    case DocumentPanelType.DOCUMENT_RECEIVED_FROM_US: {
      const applicantReceivedDocuments = newState.applicantReceivedDocuments;
      const receivedDocumentApplicantData = applicantReceivedDocuments.find((applicantReceivedDocument) => applicantReceivedDocument.applicantId === documentAttachmentListData.applicantId);
      const receivedDocument: ReceivedDocument = receivedDocumentApplicantData.receivedDocuments.find((receivedDoc) => receivedDoc.id === documentAttachmentListData.id);
      receivedDocument.attachments = [...documentAttachmentListData.attachments];
      receivedDocument.isApproved = documentAttachmentListData.isApproved;
      break;
    }
    default: {
      return newState;
    }
  }
  return newState;
}

function buildDocumentAttachmentSelection(action, state) {
  const updatedState = {...state};
  const { document, attachments } = action.payload;
  const selectedDocumentAttachmentsMap = { ...updatedState.selectedDocumentAttachmentsMap };
  selectedDocumentAttachmentsMap[ document.id ] = attachments;
  return selectedDocumentAttachmentsMap;
}

function buildDocumentActionDateData(state, documentActionDateData) {
  const newState = cloneDeep(state);
  switch (documentActionDateData.documentType) {
    case DocumentPanelType.DOCUMENT_SENT_TO_US: {
      const applicantSentDocuments = newState.applicantSentDocuments;
      const sentDocumentApplicantData = applicantSentDocuments.find((applicantSentDocument) => applicantSentDocument.applicantId === documentActionDateData.applicantId);
      const sentDocument: SentDocument = sentDocumentApplicantData.sentDocuments.find((sentDoc) => sentDoc.id === documentActionDateData.id);
      sentDocument.actionDate = documentActionDateData.actionDate;
      break;
    }
    case DocumentPanelType.DOCUMENT_RECEIVED_FROM_US: {
      const applicantReceivedDocuments = newState.applicantReceivedDocuments;
      const receivedDocumentApplicantData = applicantReceivedDocuments.find((applicantReceivedDocument) => applicantReceivedDocument.applicantId === documentActionDateData.applicantId);
      const receivedDocument: ReceivedDocument = receivedDocumentApplicantData.receivedDocuments.find((receivedDoc) => receivedDoc.id === documentActionDateData.id);
      receivedDocument.actionDate = documentActionDateData.actionDate;
      break;
    }
    default: {
      return newState;
    }
  }
  return newState;
}


function buildDocumentApprovalData(state, documentApprovalData) {
  const newState = cloneDeep(state);
  switch (documentApprovalData.documentType) {
    case DocumentPanelType.REQUIRED_DOCUMENT: {
      const requiredApplicantDocuments = newState.requireApplicantDocuments;
      const requiredDocumentApplicantData = requiredApplicantDocuments.find((requireApplicantDocument) => requireApplicantDocument.applicantId === documentApprovalData.applicantId);
      const requiredDocument: RequiredDocument = requiredDocumentApplicantData.requiredDocuments.find((requiredDoc) => requiredDoc.id === documentApprovalData.id);
      requiredDocument.isApproved = documentApprovalData.isApproved;
      break;
    }
    case DocumentPanelType.DOCUMENT_SENT_TO_US: {
      const applicantSentDocuments = newState.applicantSentDocuments;
      const sentDocumentApplicantData = applicantSentDocuments.find((applicantSentDocument) => applicantSentDocument.applicantId === documentApprovalData.applicantId);
      const sentDocument: SentDocument = sentDocumentApplicantData.sentDocuments.find((sentDoc) => sentDoc.id === documentApprovalData.id);
      sentDocument.isApproved = documentApprovalData.isApproved;
      break;
    }
    case DocumentPanelType.DOCUMENT_RECEIVED_FROM_US: {
      const applicantReceivedDocuments = newState.applicantReceivedDocuments;
      const receivedDocumentApplicantData = applicantReceivedDocuments.find((applicantReceivedDocument) => applicantReceivedDocument.applicantId === documentApprovalData.applicantId);
      const receivedDocument: ReceivedDocument = receivedDocumentApplicantData.receivedDocuments.find((receivedDoc) => receivedDoc.id === documentApprovalData.id);
      receivedDocument.isApproved = documentApprovalData.isApproved;
      break;
    }
    default: {
      return newState;
    }
  }
  return newState;
}
