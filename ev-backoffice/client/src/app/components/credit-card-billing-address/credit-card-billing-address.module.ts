import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../shared/shared.module';

import { AuthFormFieldModule } from '../auth-form-field/auth-form-field.module';
import { CreditCardBillingAddressComponent } from './credit-card-billing-address.component';


@NgModule({
  imports: [
    CommonModule,
    SharedModule,
    AuthFormFieldModule,
  ],
  declarations: [
    CreditCardBillingAddressComponent,
  ],
  exports: [
    CreditCardBillingAddressComponent,
  ]
})
export class CreditCardBillingAddressModule { }
