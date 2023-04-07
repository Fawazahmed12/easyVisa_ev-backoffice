import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { UsernamePasswordModule } from '../components/username-password/username-password.module';
import { NameEmailModule } from '../components/name-email/name-email.module';
import { AttorneySignUpComponent } from './attorney-sign-up.component';
import { AttorneySignUpRoutingModule } from './attorney-sign-up-routing.module';

@NgModule({
  imports: [
    SharedModule,
    AttorneySignUpRoutingModule,
    UsernamePasswordModule,
    NameEmailModule,
  ],
  declarations: [
    AttorneySignUpComponent,
  ],
})
export class AttorneySignUpModule { }
