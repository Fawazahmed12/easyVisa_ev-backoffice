import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NgbAccordionModule, NgbModalModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { DragToSelectModule } from 'ngx-drag-to-select';

import { SafeUrlPipeModule } from '../../shared/pipes/safe-url/safeUrlPipe.module';

import { DocumentAttachmentsComponent } from './document-attachments.component';
import { DocumentViewerModalComponent } from '../document-viewer-modal/document-viewer-modal.component';


@NgModule({
  imports: [
    CommonModule,
    DragToSelectModule.forRoot(),
    NgbAccordionModule,
    NgbModalModule,
    NgbTooltipModule,
    SafeUrlPipeModule,
  ],
  declarations: [
    DocumentAttachmentsComponent,
    DocumentViewerModalComponent
  ],
  exports: [ DocumentAttachmentsComponent ],
  entryComponents: [
    DocumentViewerModalComponent
  ],
})
export class DocumentAttachmentsModule {
}
