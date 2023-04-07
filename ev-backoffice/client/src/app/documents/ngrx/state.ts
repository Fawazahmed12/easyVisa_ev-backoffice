import { createFeatureSelector, createSelector } from '@ngrx/store';

import * as fromDocuments from './documents/documents.state';
import {
  selectDocumentAttachmentsSelectedState,
  selectDocumentsState,
  selectFileUploadingState,
  selectReceivedDocuments,
  selectRequireDocumentEntities,
  selectDocumentPortalAccessState,
  selectRequireDocumentItems,
  selectRequireDocuments,
  selectSentDocuments,
  selectOpenAllSentPanelsState,
  selectOpenAllReceivedPanelsState,
  selectFileDownloadingState,
  selectDocumentAccessState
} from './documents/documents.state';
import * as fromDocumentsRequest from './documents-requests/state';
import {
  selectApplicantReceivedDocumentsGetState,
  selectApplicantSentDocumentsGetState, selectDocumentAccessGetState,
  selectDocumentActionDatePostState, selectDocumentApprovalPatchState,
  selectDocumentAttachmentPostState,
  selectDocumentAttachmentsDeleteState, selectDocumentPortalAccessGetState,
  selectDocumentsRequestState,
  selectRequiredDocumentsGetState
} from './documents-requests/state';
import * as fromNotes from './notes/notes.state';
import * as fromFormsSheets from './forms-sheets/forms-sheets.state';
import * as fromFormsSheetsRequest from './forms-sheets-requests/state';
import * as fromMilestoneDates from './milestone-dates/milestone-dates.state';
import * as fromMilestoneDatesRequest from './milestones-dates-requests/state';
import { selectNotesState, selectPublicNotes, selectRepresentativeNotes } from './notes/notes.state';
import * as fromNotesRequest from './notes-requests/state';
import {
  selectNoteDeleteRequestState,
  selectNoteModuleRequestsState,
  selectNotePostRequestState,
  selectNotesGetRequestState
} from './notes-requests/state';
import {
  selectBlanks,
  selectFormsSheetsState,
  selectPackageApplicants,
  selectPackageContinuationSheets,
  selectPackageForms, selectSelectedApplicants, selectSelectedApplicantsIds
} from './forms-sheets/forms-sheets.state';
import {
  selectBlanksGetRequestState, selectDownloadBlanksGetRequestState,
  selectDownloadFormsGetRequestState,
  selectFormsSheetsGetRequestState,
  selectFormsSheetsModuleRequestsState, selectPrintBlankGetRequestState,
  selectPrintFormGetRequestState
} from './forms-sheets-requests/state';
import { selectMilestoneDates, selectMilestoneDatesState } from './milestone-dates/milestone-dates.state';
import {
  selectMilestoneDatePostRequestState,
  selectMilestoneDatesGetRequestState,
  selectMilestoneDatesModuleRequestsState
} from './milestones-dates-requests/state';


export interface State {
  [ fromDocuments.DOCUMENTS_STATE ]: fromDocuments.DocumentsState;
  [ fromDocumentsRequest.DOCUMENTS_REQUEST ]: fromDocumentsRequest.DocumentsRequestState;
  [ fromNotes.NOTES ]: fromNotes.NotesState;
  [ fromNotesRequest.NOTES_REQUESTS ]: fromNotesRequest.NotesRequestState;
  [ fromFormsSheets.FORMS_SHEETS ]: fromFormsSheets.FormsSheetsState;
  [ fromFormsSheetsRequest.FORMS_SHEETS_REQUESTS ]: fromFormsSheetsRequest.FormsSheetsRequestState;
  [ fromMilestoneDates.MILESTONE_DATES ]: fromMilestoneDates.MilestoneDatesState;
  [ fromMilestoneDatesRequest.MILESTONES_DATES_REQUESTS ]: fromMilestoneDatesRequest.MilestoneDatesRequestState;
}

export const DOCUMENTS_MODULE_STATE = 'DocumentsModuleState';
export const selectDocumentsModuleState = createFeatureSelector<State>(DOCUMENTS_MODULE_STATE);

export const getDocumentsState = createSelector(
  selectDocumentsModuleState,
  selectDocumentsState,
);

export const getDocumentPortalAccessState = createSelector(
  getDocumentsState,
  selectDocumentPortalAccessState,
);

export const getRequiredDocumentEntities = createSelector(
  getDocumentsState,
  selectRequireDocumentEntities,
);

export const getRequiredDocumentItems = createSelector(
  getDocumentsState,
  selectRequireDocumentItems,
);

export const getDocumentsRequestState = createSelector(
  selectDocumentsModuleState,
  selectDocumentsRequestState,
);

export const getDocumentPortalAccessRequestState = createSelector(
  getDocumentsRequestState,
  selectDocumentPortalAccessGetState,
);


export const getRequiredDocumentRequestState = createSelector(
  getDocumentsRequestState,
  selectRequiredDocumentsGetState,
);

export const getRequiredDocuments = createSelector(
  getDocumentsState,
  selectRequireDocuments,
);

export const postRequiredDocRequestState = createSelector(
  getDocumentsRequestState,
  selectDocumentAttachmentPostState,
);

export const getDocumentAttachmentsSelectedState = createSelector(
  getDocumentsState,
  selectDocumentAttachmentsSelectedState,
);

export const getFileUploadingState = createSelector(
  getDocumentsState,
  selectFileUploadingState,
);

export const getFileDownloadingState = createSelector(
  getDocumentsState,
  selectFileDownloadingState,
);

export const getOpenAllSentPanelsState = createSelector(
  getDocumentsState,
  selectOpenAllSentPanelsState,
);


export const getOpenAllReceivedPanelsState = createSelector(
  getDocumentsState,
  selectOpenAllReceivedPanelsState,
);

export const deleteDocumentAttachmentsRequestState = createSelector(
  getDocumentsRequestState,
  selectDocumentAttachmentsDeleteState
);

export const getNotesState = createSelector(
  selectDocumentsModuleState,
  selectNotesState,
);

export const getNotesRequestState = createSelector(
  selectDocumentsModuleState,
  selectNoteModuleRequestsState,
);

export const getPublicNotes = createSelector(
  getNotesState,
  selectPublicNotes,
);

export const getRepresentativeNotes = createSelector(
  getNotesState,
  selectRepresentativeNotes,
);

export const getNotesGetRequestState = createSelector(
  getNotesRequestState,
  selectNotesGetRequestState,
);

export const getNotePostRequestState = createSelector(
  getNotesRequestState,
  selectNotePostRequestState,
);

export const getNoteDeleteRequestState = createSelector(
  getNotesRequestState,
  selectNoteDeleteRequestState,
);

export const getFormsSheetsState = createSelector(
  selectDocumentsModuleState,
  selectFormsSheetsState,
);

export const getFormsSheetsRequestState = createSelector(
  selectDocumentsModuleState,
  selectFormsSheetsModuleRequestsState,
);

export const getFormsSheetsGetRequestState = createSelector(
  getFormsSheetsRequestState,
  selectFormsSheetsGetRequestState,
);

export const getPackageApplicants = createSelector(
  getFormsSheetsState,
  selectPackageApplicants,
);

export const getSelectedApplicants = createSelector(
  getFormsSheetsState,
  selectSelectedApplicants
);

export const getSelectedApplicantsIds = createSelector(
  getFormsSheetsState,
  selectSelectedApplicantsIds
);

export const getAllPackageForms = createSelector(
  getFormsSheetsState,
  selectPackageForms,
);

export const getAllContinuationSheets = createSelector(
  getFormsSheetsState,
  selectPackageContinuationSheets,
);

export const getCurrentPackageForms = createSelector(
  getFormsSheetsState,
  selectPackageForms,
);

export const getCurrentContinuationSheets = createSelector(
  getFormsSheetsState,
  selectPackageContinuationSheets,
);

export const getMilestoneDatesState = createSelector(
  selectDocumentsModuleState,
  selectMilestoneDatesState,
);

export const getMilestoneDatesRequestState = createSelector(
  selectDocumentsModuleState,
  selectMilestoneDatesModuleRequestsState,
);

export const getMilestoneDatesGetRequestState = createSelector(
  getMilestoneDatesRequestState,
  selectMilestoneDatesGetRequestState,
);

export const getMilestoneDatePostRequestState = createSelector(
  getMilestoneDatesRequestState,
  selectMilestoneDatePostRequestState,
);

export const getMilestoneDates = createSelector(
  getMilestoneDatesState,
  selectMilestoneDates,
);

export const getPrintFormGetRequestState = createSelector(
  getFormsSheetsRequestState,
  selectPrintFormGetRequestState,
);

export const getDownloadFormsGetRequestState = createSelector(
  getFormsSheetsRequestState,
  selectDownloadFormsGetRequestState,
);

export const getBlanksGetRequestState = createSelector(
  getFormsSheetsRequestState,
  selectBlanksGetRequestState,
);

export const getBlanks = createSelector(
  getFormsSheetsState,
  selectBlanks,
);

export const getDownloadBlanksGetRequestState = createSelector(
  getFormsSheetsRequestState,
  selectDownloadBlanksGetRequestState,
);

export const getPrintBlankGetRequestState = createSelector(
  getFormsSheetsRequestState,
  selectPrintBlankGetRequestState,
);


export const getSentDocumentRequestState = createSelector(
  getDocumentsRequestState,
  selectApplicantSentDocumentsGetState
);

export const getSentDocuments = createSelector(
  getDocumentsState,
  selectSentDocuments,
);

export const postDocumentActionDateRequestState = createSelector(
  getDocumentsRequestState,
  selectDocumentActionDatePostState,
);

export const getReceivedDocumentRequestState = createSelector(
  getDocumentsRequestState,
  selectApplicantReceivedDocumentsGetState
);

export const getReceivedDocuments = createSelector(
  getDocumentsState,
  selectReceivedDocuments,
);

export const patchDocumentApprovalRequestState = createSelector(
  getDocumentsRequestState,
  selectDocumentApprovalPatchState,
);

export const getDocumentAccessState = createSelector(
  getDocumentsState,
  selectDocumentAccessState,
);

export const getDocumentAccessRequestState = createSelector(
  getDocumentsRequestState,
  selectDocumentAccessGetState,
);


