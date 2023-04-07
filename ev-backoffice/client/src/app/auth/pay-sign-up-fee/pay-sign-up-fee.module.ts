import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { CreditCardInfoModule } from '../../components/credit-card-info/credit-card-info.module';
import { AuthFormFieldModule } from '../../components/auth-form-field/auth-form-field.module';
import { AddressModule } from '../../components/address/address.module';
import { PaySignUpFeeComponent } from './pay-sign-up-fee.component';
import { PaySignUpFeeRoutingModule } from './pay-sign-up-fee-routing.module';
import { TermsConditionsModalComponent } from './terms-conditions-modal/terms-conditions-modal.component';
import { CreditCardBillingAddressModule } from '../../components/credit-card-billing-address/credit-card-billing-address.module';
import { TaxesInfoModule } from '../../components/taxes-info/taxes-info.module';
import { PaymentFailedModalModule } from '../../core/modals';
import { TermsConditionsConfirmationModalComponent } from './terms-conditions-confirmation-modal/terms-conditions-confirmation-modal.component';
import { SpinnerModule } from '../../components/spinner/spinner.module';

@NgModule({
    imports: [
        SharedModule,
        AuthFormFieldModule,
        AddressModule,
        CreditCardInfoModule,
        PaySignUpFeeRoutingModule,
        CreditCardBillingAddressModule,
        PaymentFailedModalModule,
        TaxesInfoModule,
        SpinnerModule,
    ],
  declarations: [
    PaySignUpFeeComponent,
    TermsConditionsModalComponent,
    TermsConditionsConfirmationModalComponent
  ]
})
export class PaySignUpFeeModule {
}
