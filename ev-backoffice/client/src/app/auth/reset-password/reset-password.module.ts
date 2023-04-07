import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { PasswordMeterModule } from '../../components/password-meter/password-meter.module';
import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';

import { ResetPasswordRoutingModule } from './reset-password-routing.module';
import { ResetPasswordComponent } from './reset-password.component';
import { ResetPasswordService } from './reset-password.service';

@NgModule({
  imports: [
    SharedModule,
    ResetPasswordRoutingModule,
    PasswordMeterModule,
    AuthWrapperModule,
  ],
  declarations: [
    ResetPasswordComponent,
  ],
  providers: [
    ResetPasswordService,
  ],
})
export class ResetPasswordModule { }
