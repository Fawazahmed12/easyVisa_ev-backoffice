import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';
import { CannotDeletePackagesModalComponent } from './cannot-delete-packages-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule
  ],
  declarations: [
    CannotDeletePackagesModalComponent,
  ],
  exports: [
    CannotDeletePackagesModalComponent,
  ],
  entryComponents: [
    CannotDeletePackagesModalComponent,
  ],
})

export class CannotDeletePackagesModalModule {
}
