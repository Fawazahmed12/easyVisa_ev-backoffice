import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { CreditCardBillingAddressModule } from '../../../components/credit-card-billing-address/credit-card-billing-address.module';
import { CreditCardInfoModule } from '../../../components/credit-card-info/credit-card-info.module';

import { PaymentMethodComponent } from './payment-method.component';
import { CreditCardInfoViewModule } from '../../../components/credit-card-info-view/credit-card-info-view.module';

@NgModule({
  imports: [
    SharedModule,
    CreditCardBillingAddressModule,
    CreditCardInfoModule,
    CreditCardInfoViewModule,
  ],
  declarations: [
    PaymentMethodComponent,
  ],
  exports: [
    PaymentMethodComponent,
  ]
})
export class PaymentMethodModule {
}
