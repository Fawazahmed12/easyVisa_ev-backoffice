import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ConfirmDeleteLeadsModalComponent } from './confirm-delete-leads-modal.component';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    ConfirmDeleteLeadsModalComponent,
  ],
  exports: [
    ConfirmDeleteLeadsModalComponent,
  ],
  entryComponents: [
    ConfirmDeleteLeadsModalComponent,
  ],
})

export class ConfirmDeleteLeadsModalModule {
}
