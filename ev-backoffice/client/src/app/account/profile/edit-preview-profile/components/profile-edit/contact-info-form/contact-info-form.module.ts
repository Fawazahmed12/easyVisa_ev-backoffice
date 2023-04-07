import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';
import { PhoneFieldModule } from '../../../../../../components/phone-field/phone-field.module';

import { ContactInfoFormComponent } from './contact-info-form.component';

@NgModule({
  imports: [
    SharedModule,
    PhoneFieldModule,
  ],
  declarations: [
    ContactInfoFormComponent,
  ],
  exports: [
    ContactInfoFormComponent,
  ]
})

export class ContactInfoFormModule {
}
