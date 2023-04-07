import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { PetitionerStatusComponent } from './petitioner-status.component';
import { StatusChangeWarningModalComponent } from './status-change-warning-modal/status-change-warning-modal.component';

@NgModule({
  imports: [
    SharedModule,
  ],
  declarations: [
    PetitionerStatusComponent,
    StatusChangeWarningModalComponent,
  ],
  exports: [
    PetitionerStatusComponent,
  ]
})
export class PetitionerStatusModule {
}
