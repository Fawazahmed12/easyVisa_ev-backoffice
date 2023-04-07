import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { RegistrationComponent } from './registration.component';
import { RegistrationRoutingModule } from './registration-routing.module';

@NgModule({
  imports: [
    SharedModule,
    RegistrationRoutingModule,
  ],
  declarations: [
    RegistrationComponent,
  ]
})
export class RegistrationModule { }
