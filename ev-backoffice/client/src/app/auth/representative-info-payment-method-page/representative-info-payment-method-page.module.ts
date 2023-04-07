import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { RepresentativeTypePipeModule } from '../../shared/pipes/representative-type/representative-type-pipe.module';
import { AuthFormFieldModule } from '../../components/auth-form-field/auth-form-field.module';
import { PhoneFieldModule } from '../../components/phone-field/phone-field.module';
import { AddressModule } from '../../components/address/address.module';
import { CreditCardInfoModule } from '../../components/credit-card-info/credit-card-info.module';
import { CreditCardBillingAddressModule } from '../../components/credit-card-billing-address/credit-card-billing-address.module';
import { TaxesInfoModule } from '../../components/taxes-info/taxes-info.module';

import { RepresentativeInfoPaymentMethodPageRoutingModule } from './representative-info-payment-method-page-routing.module';
import { RepresentativeInfoPaymentMethodPageComponent } from './representative-info-payment-method-page.component';

@NgModule({
  imports: [
    SharedModule,
    AddressModule,
    PhoneFieldModule,
    AuthFormFieldModule,
    CreditCardInfoModule,
    RepresentativeTypePipeModule,
    CreditCardBillingAddressModule,
    RepresentativeInfoPaymentMethodPageRoutingModule,
    TaxesInfoModule,
  ],
  declarations: [
    RepresentativeInfoPaymentMethodPageComponent,
  ]
})
export class RepresentativeInfoPaymentMethodPageModule {
}
