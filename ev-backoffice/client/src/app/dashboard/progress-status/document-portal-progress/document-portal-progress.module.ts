import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../../shared/shared.module';
import { ProgressUnitModule } from '../progress-unit/progress-unit.module';
import { DocumentPortalProgressComponent } from './document-portal-progress.component';

@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    ProgressUnitModule
  ],
  declarations: [DocumentPortalProgressComponent],
  exports: [DocumentPortalProgressComponent]
})
export class DocumentPortalProgressModule { }
