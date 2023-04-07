import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../../../components/modal-header/modal-header.module';

import { ImportantMessageModalComponent } from './important-message-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    ImportantMessageModalComponent,
  ],
  exports: [
    ImportantMessageModalComponent,
  ],
  entryComponents: [
    ImportantMessageModalComponent,
  ],
})

export class ImportantMessageModalModule {
}
