import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { AuthFormFieldModule } from '../../../components/auth-form-field/auth-form-field.module';
import { ValueExistenceMessagesModule } from '../value-existence-messages/value-existence-messages.module';

import { NameEmailComponent } from './name-email.component';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  imports: [
    SharedModule,
    AuthFormFieldModule,
    ReactiveFormsModule,
    ValueExistenceMessagesModule,
  ],
  declarations: [
    NameEmailComponent,
  ],
  exports: [
    NameEmailComponent,
  ]
})
export class NameEmailModule {
}
