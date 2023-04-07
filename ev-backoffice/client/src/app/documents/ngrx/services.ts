import { DocumentsRequestService } from './documents-requests/request.service';
import { NotesRequestService } from './notes-requests/request.service';
import { FormsSheetsRequestService } from './forms-sheets-requests/request.service';
import { MilestoneDatesRequestService } from './milestones-dates-requests/request.service';

export const DOCUMENTS_NGRX_PROVIDERS = [
  DocumentsRequestService,
  NotesRequestService,
  FormsSheetsRequestService,
  MilestoneDatesRequestService
];

export { DocumentsRequestService } from './documents-requests/request.service';
export { NotesRequestService } from './notes-requests/request.service';
export { FormsSheetsRequestService } from './forms-sheets-requests/request.service';
export { MilestoneDatesRequestService } from './milestones-dates-requests/request.service';
