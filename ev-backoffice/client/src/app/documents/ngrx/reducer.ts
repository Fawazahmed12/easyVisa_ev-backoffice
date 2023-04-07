import { ActionReducerMap } from '@ngrx/store';

import { State } from './state';

import * as fromDocuments from './documents/documents.reducers';
import * as fromNotes from './notes/notes.reducers';
import * as fromFormsSheets from './forms-sheets/forms-sheets.reducers';
import * as fromMilestoneDates from './milestone-dates/milestone-dates.reducers';
import * as fromDocumentsRequest from './documents-requests/reducer';
import * as fromNotesRequest from './notes-requests/reducer';
import * as fromFormsSheetsRequest from './forms-sheets-requests/reducer';
import * as fromMilestoneDatesRequest from './milestones-dates-requests/reducer';

export const reducers: ActionReducerMap<State> = {
  Documents: fromDocuments.reducer,
  DocumentsRequests: fromDocumentsRequest.reducer,
  Notes: fromNotes.reducer,
  NotesRequests: fromNotesRequest.reducer,
  FormsSheets: fromFormsSheets.reducer,
  FormsSheetsRequests: fromFormsSheetsRequest.reducer,
  MilestoneDates: fromMilestoneDates.reducer,
  MilestoneDatesRequests: fromMilestoneDatesRequest.reducer
};
