import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { PaymentWarningComponent } from './payment-warning.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
  ],
  declarations: [
    PaymentWarningComponent,
  ],
  exports: [
    PaymentWarningComponent,
  ],
  entryComponents: [
    PaymentWarningComponent,
  ]
})
export class PaymentWarningModule { }
