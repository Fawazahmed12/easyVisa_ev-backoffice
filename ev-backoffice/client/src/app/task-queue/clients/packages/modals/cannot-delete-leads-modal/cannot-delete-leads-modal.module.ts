import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';
import { CannotDeleteLeadsModalComponent } from './cannot-delete-leads-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule
  ],
  declarations: [
    CannotDeleteLeadsModalComponent,
  ],
  exports: [
    CannotDeleteLeadsModalComponent,
  ],
  entryComponents: [
    CannotDeleteLeadsModalComponent,
  ],
})

export class CannotDeleteLeadsModalModule {
}
