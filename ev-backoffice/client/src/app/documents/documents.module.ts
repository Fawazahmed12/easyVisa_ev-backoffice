import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../shared/shared.module';

import { GUARD_PROVIDERS } from './guards';
import { RESOLVERS } from './resolvers';
import { PROVIDERS } from './services';

import { DocumentsComponent } from './documents.component';
import { DocumentsRoutingModule } from './documents-routing.module';
import { NgrxDocumentsModule } from './ngrx/module';

import { VisaInterviewDocumentationPanelModule } from './visa-interview-documentation-panel/visa-interview-documentation-panel.module';
import { RequiredDocumentsPanelModule } from './required-documents-panel/required-documents-panel.module';
import { RepresentativeNotesModule } from './representative-notes/representative-notes.module';
import { MilestoneDatesModule } from './milestone-dates/milestone-dates.module';
import { PrintPackageFormsModule } from './print-package-forms/print-package-forms.module';
import { SentDocumentsPanelModule } from './sent-documents-panel/sent-documents-panel.module';
import { ReceivedDocumentsPanelModule } from './received-documents-panel/received-documents-panel.module';
import { SpinnerModule } from '../components/spinner/spinner.module';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    DocumentsRoutingModule,
    NgrxDocumentsModule,
    RequiredDocumentsPanelModule,
    VisaInterviewDocumentationPanelModule,
    RepresentativeNotesModule,
    MilestoneDatesModule,
    PrintPackageFormsModule,
    SentDocumentsPanelModule,
    ReceivedDocumentsPanelModule,
    SpinnerModule
  ],
  declarations: [
    DocumentsComponent,
  ],
  providers: [
    GUARD_PROVIDERS,
    RESOLVERS,
    PROVIDERS
  ]
})
export class DocumentsModule {
}
