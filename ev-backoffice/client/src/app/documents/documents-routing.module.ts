import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DocumentsComponent } from './documents.component';
import { I18nResolverService } from '../core/i18n/i18n-resolver.service';

import { DocumentsResolverService } from './resolvers/documents-resolver.service';
import { NotesResolverService } from './resolvers/notes-resolver.service';
import { PrintFormsSheetsResolverService } from './resolvers/print-forms-sheets-resolver.service';
import { MilestoneDatesResolverService } from './resolvers/milestone-dates-resolver.service';
import { SentDocumentsResolverService } from './resolvers/sent-documents-resolver.service';
import { ReceivedDocumentsResolverService } from './resolvers/received-documents-resolver.service';
import { ActiveDocumentPortalGuardService } from './guards/active-documentportal-guard.service';
import { DocumentsAccessResolverService } from './resolvers/documents-access-resolver.service';

export const routes: Routes = [
  {
    path: 'package/:packageId',
    component: DocumentsComponent,
    resolve: {
      translation: I18nResolverService,
      requiredDocumentList: DocumentsResolverService,
      sentDocumentList: SentDocumentsResolverService,
      representativeNotes: NotesResolverService,
      printFormsSheets: PrintFormsSheetsResolverService,
      milestoneDates: MilestoneDatesResolverService,
      receivedDocumentList: ReceivedDocumentsResolverService,
      documentAccess: DocumentsAccessResolverService,
    },
    data: {
      translationUrl: 'documents-module',
    },
    canActivate: [ ActiveDocumentPortalGuardService ],
  }
];

@NgModule({
  imports: [ RouterModule.forChild(routes) ],
  exports: [ RouterModule ],
})
export class DocumentsRoutingModule {
}
