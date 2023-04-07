import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ConfirmDeletePackagesModalComponent } from './confirm-delete-packages-modal.component';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    ConfirmDeletePackagesModalComponent,
  ],
  exports: [
    ConfirmDeletePackagesModalComponent,
  ],
  entryComponents: [
    ConfirmDeletePackagesModalComponent,
  ],
})

export class ConfirmDeletePackagesModalModule {
}
