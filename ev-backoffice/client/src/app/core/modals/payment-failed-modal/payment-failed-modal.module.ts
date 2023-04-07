import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';
import { SafeHtmlPipeModule } from '../../../shared/pipes/safe-html/safeHtmlPipe.module';

import { PaymentFailedModalComponent } from './payment-failed-modal.component';



@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
    SafeHtmlPipeModule,
  ],
  declarations: [
    PaymentFailedModalComponent,
  ],
  exports: [
    PaymentFailedModalComponent,
  ],
  entryComponents: [
    PaymentFailedModalComponent,
  ],
})

export class PaymentFailedModalModule {
}
