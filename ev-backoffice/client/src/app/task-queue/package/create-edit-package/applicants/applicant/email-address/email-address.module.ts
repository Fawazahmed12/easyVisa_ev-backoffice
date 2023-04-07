import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { HorizontalFormFieldModule } from '../../../components/horizontal-form-field/horizontal-form-field.module';

import { EmailAddressComponent } from './email-address.component';
import { MODALS } from './modals';

@NgModule({
  imports: [
    SharedModule,
    HorizontalFormFieldModule,
  ],
  declarations: [
    EmailAddressComponent,
    MODALS,
  ],
  exports: [
    EmailAddressComponent,
  ]
})
export class EmailAddressModule {
}
