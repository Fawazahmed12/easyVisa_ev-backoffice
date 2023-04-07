import { DocumentsResolverService } from './documents-resolver.service';
import { NotesResolverService } from './notes-resolver.service';
import { SentDocumentsResolverService } from './sent-documents-resolver.service';
import { PrintFormsSheetsResolverService } from './print-forms-sheets-resolver.service';
import { MilestoneDatesResolverService } from './milestone-dates-resolver.service';
import { ReceivedDocumentsResolverService } from './received-documents-resolver.service';
import { DocumentsAccessResolverService } from './documents-access-resolver.service';

export const RESOLVERS = [
  DocumentsResolverService,
  NotesResolverService,
  PrintFormsSheetsResolverService,
  MilestoneDatesResolverService,
  SentDocumentsResolverService,
  ReceivedDocumentsResolverService,
  DocumentsAccessResolverService
];
