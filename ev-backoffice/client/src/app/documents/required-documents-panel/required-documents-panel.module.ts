import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbAccordionModule, NgbModalModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { UploadDocumentModule } from '../upload-document/upload-document.module';
import { DragDropDocumentModule } from '../drag-drop-document/drag-drop-document.module';
import { DocumentAttachmentsModule } from '../document-attachments/document-attachments.module';
import { DocumentHelpModule } from '../document-help/document-help.module';
import { DocumentDispositionModule } from '../document-disposition/document-disposition.module';

import { RequiredDocumentsPanelComponent } from './required-documents-panel.component';
import { RequiredDocumentComponent } from './required-document/required-document.component';
import { RequiredDocumentHelpComponent } from './required-document-help/required-document-help.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    NgbAccordionModule,
    NgbModalModule,
    UploadDocumentModule,
    DragDropDocumentModule,
    DocumentAttachmentsModule,
    DocumentHelpModule,
    DocumentDispositionModule
  ],
  declarations: [
    RequiredDocumentsPanelComponent,
    RequiredDocumentComponent,
    RequiredDocumentHelpComponent,
  ],
  exports: [
    RequiredDocumentsPanelComponent
  ],
  entryComponents: [
    RequiredDocumentHelpComponent
  ]
})
export class RequiredDocumentsPanelModule {
}
