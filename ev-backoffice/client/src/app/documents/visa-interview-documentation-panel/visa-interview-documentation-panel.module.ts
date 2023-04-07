import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NgbAccordionModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';
import { DocumentHelpModule } from '../document-help/document-help.module';

import { VisaInterviewDocumentationPanelComponent } from './visa-interview-documentation-panel.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    SharedModule,
    NgbAccordionModule,
    DocumentHelpModule
  ],
  declarations: [
    VisaInterviewDocumentationPanelComponent,
  ],
  exports: [
    VisaInterviewDocumentationPanelComponent
  ]
})
export class VisaInterviewDocumentationPanelModule {
}
