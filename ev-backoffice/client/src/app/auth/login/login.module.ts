import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { AuthWrapperModule } from '../components/auth-wrapper/auth-wrapper.module';

import { LoginRoutingModule } from './login-routing.module';
import { LoginComponent } from './login.component';

@NgModule({
  imports: [
    SharedModule,
    LoginRoutingModule,
    AuthWrapperModule,
  ],
  declarations: [
    LoginComponent,
  ]
})
export class LoginModule { }
