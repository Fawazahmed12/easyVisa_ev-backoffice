import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { UsernamePasswordModule } from '../components/username-password/username-password.module';
import { NameEmailModule } from '../components/name-email/name-email.module';

import { SignUpRoutingModule } from './sign-up-routing.module';
import { SignUpComponent } from './sign-up.component';
import { SignUpResolverService } from './sign-up-resolver.service';

@NgModule({
  imports: [
    SharedModule,
    SignUpRoutingModule,
    UsernamePasswordModule,
    NameEmailModule,
  ],
  declarations: [
    SignUpComponent,
  ],
  providers: [
    SignUpResolverService
  ],
})
export class SignUpModule { }
