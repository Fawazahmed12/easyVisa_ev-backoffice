import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';
import { TimePeriodModule } from '../../../../../components/time-period/time-period.module';

import { DeleteOldLeadsModalComponent } from './delete-old-leads-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    TimePeriodModule
  ],
  declarations: [
    DeleteOldLeadsModalComponent,
  ],
  exports: [
    DeleteOldLeadsModalComponent,
  ],
  entryComponents: [
    DeleteOldLeadsModalComponent,
  ],
})

export class DeleteOldLeadsModalModule {
}
