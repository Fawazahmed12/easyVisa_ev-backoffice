import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DragToSelectModule } from 'ngx-drag-to-select';
import { NgbAccordionModule, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';
import { DragDropDocumentModule } from '../drag-drop-document/drag-drop-document.module';
import { UploadDocumentModule } from '../upload-document/upload-document.module';
import { SafeUrlPipeModule } from '../../shared/pipes/safe-url/safeUrlPipe.module';
import { DocumentAttachmentsModule } from '../document-attachments/document-attachments.module';
import { DocumentHelpModule } from '../document-help/document-help.module';
import { DocumentDispositionModule } from '../document-disposition/document-disposition.module';

import { ReceivedDocumentsPanelComponent } from './received-documents-panel.component';
import { ReceivedDocumentComponent } from './received-document/received-document.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    NgbAccordionModule,
    NgbDatepickerModule,
    UploadDocumentModule,
    DragDropDocumentModule,
    SafeUrlPipeModule,
    DragToSelectModule,
    DocumentAttachmentsModule,
    DocumentHelpModule,
    DocumentDispositionModule
  ],
  declarations: [ ReceivedDocumentsPanelComponent, ReceivedDocumentComponent ],
  exports: [ ReceivedDocumentsPanelComponent ]
})
export class ReceivedDocumentsPanelModule {
}
