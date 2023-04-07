import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';

import { SharedModule } from '../../shared/shared.module';

import { DocumentHelpComponent } from './document-help.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    NgbTooltipModule
  ],
  declarations: [ DocumentHelpComponent ],
  exports: [ DocumentHelpComponent ]
})
export class DocumentHelpModule {
}
