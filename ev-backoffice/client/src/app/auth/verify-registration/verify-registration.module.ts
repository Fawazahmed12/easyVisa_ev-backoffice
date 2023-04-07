import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { SpinnerModule } from '../../components/spinner/spinner.module';

import { VerifyRegistrationRoutingModule } from './verify-registration-routing.module';
import { VerifyRegistrationComponent } from './verify-registration.component';
import { VerifyRegistrationGuardService } from './verify-registration-guard.service';

@NgModule({
  imports: [
    SharedModule,
    VerifyRegistrationRoutingModule,
    SpinnerModule,
  ],
  declarations: [
    VerifyRegistrationComponent,
  ],
  providers: [
    VerifyRegistrationGuardService,
  ]
})
export class VerifyRegistrationModule {
}
