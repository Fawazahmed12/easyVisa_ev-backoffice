import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../../../components/modal-header/modal-header.module';

import { PermanentlyDeleteModalComponent } from './permanently-delete-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    PermanentlyDeleteModalComponent,
  ],
  exports: [
    PermanentlyDeleteModalComponent,
  ],
  entryComponents: [
    PermanentlyDeleteModalComponent,
  ]
})

export class PermanentlyDeleteModalModule {
}
