import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';
import { SignUpService } from '../../../../../../../auth/services';
import { ChangePasswordComponent } from './change-password.component';
import { PasswordMeterModule } from '../../../../../../../components/password-meter/password-meter.module';
import { AuthFormFieldModule } from '../../../../../../../components/auth-form-field/auth-form-field.module';
import { AuthService } from '../../../../../../../core/services';



@NgModule({
  imports: [
    SharedModule,
    PasswordMeterModule,
    AuthFormFieldModule,
  ],
  declarations: [
    ChangePasswordComponent,
  ],
  exports: [
    ChangePasswordComponent,
  ],
  providers: [
    AuthService
  ]
})

export class ChangePasswordModule {
}
