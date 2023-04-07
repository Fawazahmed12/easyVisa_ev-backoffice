import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../components/modal-header/modal-header.module';

import { RequestTransferSentModalComponent } from './request-transfer-sent-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    RequestTransferSentModalComponent,
  ],
  exports: [
    RequestTransferSentModalComponent,
  ],
  entryComponents: [
    RequestTransferSentModalComponent,
  ]
})

export class RequestTransferSentModalModule {
}
