import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';

import { UsernamePasswordComponent } from './username-password.component';
import { AuthFormFieldModule } from '../../../components/auth-form-field/auth-form-field.module';
import { ValueExistenceMessagesModule } from '../value-existence-messages/value-existence-messages.module';
import { PasswordMeterModule } from '../../../components/password-meter/password-meter.module';

@NgModule({
  imports: [
    SharedModule,
    AuthFormFieldModule,
    ValueExistenceMessagesModule,
    PasswordMeterModule,
  ],
  declarations: [
    UsernamePasswordComponent,
  ],
  exports: [
    UsernamePasswordComponent,
  ]
})
export class UsernamePasswordModule {
}
