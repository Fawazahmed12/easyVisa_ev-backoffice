import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';
import { SignUpSuccessRoutingModule } from './sign-up-success-routing.module';
import { SignUpSuccessComponent } from './sign-up-success.component';

@NgModule({
  imports: [
    SharedModule,
    SignUpSuccessRoutingModule,
    AuthWrapperModule,
  ],
  declarations: [
    SignUpSuccessComponent,
  ],
})
export class SignUpSuccessModule {
}
