import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';
import { CannotTransferModalComponent } from './cannot-transfer-modal.component';
import { RepresentativeTypePipeModule } from '../../../../../shared/pipes/representative-type/representative-type-pipe.module';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    RepresentativeTypePipeModule,
  ],
  declarations: [
    CannotTransferModalComponent,
  ],
  exports: [
    CannotTransferModalComponent,
  ],
  entryComponents: [
    CannotTransferModalComponent,
  ],
})

export class CannotTransferModalModule {
}
