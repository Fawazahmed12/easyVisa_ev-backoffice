import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbAccordionModule, NgbDatepickerModule } from '@ng-bootstrap/ng-bootstrap';
import { DragToSelectModule } from 'ngx-drag-to-select';

import { SharedModule } from '../../shared/shared.module';
import { SafeUrlPipeModule } from '../../shared/pipes/safe-url/safeUrlPipe.module';


import { UploadDocumentModule } from '../upload-document/upload-document.module';
import { DragDropDocumentModule } from '../drag-drop-document/drag-drop-document.module';
import { DocumentAttachmentsModule } from '../document-attachments/document-attachments.module';

import { SentDocumentsPanelComponent } from './sent-documents-panel.component';
import { SentDocumentComponent } from './sent-document/sent-document.component';


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
    DocumentAttachmentsModule
  ],
  declarations: [ SentDocumentsPanelComponent, SentDocumentComponent ],
  exports: [ SentDocumentsPanelComponent ]
})
export class SentDocumentsPanelModule {
}
