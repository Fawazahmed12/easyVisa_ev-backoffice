import { DocumentsEffects } from './documents/documents.effects';
import { DocumentsRequestEffects } from './documents-requests/effects';
import { NotesEffects } from './notes/notes.effects';
import { NotesRequestEffects } from './notes-requests/effects';
import { FormsSheetsEffects } from './forms-sheets/forms-sheets.effects';
import { FormsSheetsRequestEffects } from './forms-sheets-requests/effects';
import { MilestoneDatesEffects } from './milestone-dates/milestone-dates.effects';
import { MilestoneDatesRequestEffects } from './milestones-dates-requests/effects';

export const effects = [
  DocumentsEffects,
  NotesEffects,
  FormsSheetsEffects,
  MilestoneDatesEffects,
  ...DocumentsRequestEffects,
  ...NotesRequestEffects,
  ...FormsSheetsRequestEffects,
  ...MilestoneDatesRequestEffects
];
