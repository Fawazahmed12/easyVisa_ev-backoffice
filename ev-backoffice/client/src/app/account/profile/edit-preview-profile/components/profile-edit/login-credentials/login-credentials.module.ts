import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';
import { BlockModule } from '../../../../../../shared/components/block/block.module';

import { LoginCredentialsComponent } from './login-credentials.component';
import { ChangeEmailModule } from './change-email/change-email.module';
import { ChangePasswordModule } from './change-password/change-password.module';
import { PasswordMeterModule } from '../../../../../../components/password-meter/password-meter.module';


@NgModule({
  imports: [
    SharedModule,
    BlockModule,
    ChangeEmailModule,
    ChangePasswordModule,
    PasswordMeterModule,
  ],
  declarations: [
    LoginCredentialsComponent,
  ],
  exports: [
    LoginCredentialsComponent,
  ]
})

export class LoginCredentialsModule {
}
