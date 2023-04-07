import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';

import { StandardEvChargesComponent } from './standard-ev-charges.component';
import { StandardEvChargesRoutingModule } from './standard-ev-charges-routing.module';
import { PerApplicantChargesModalComponent } from './per-applicant-charges-modal/per-applicant-charges-modal.component';

@NgModule({
  imports: [
    SharedModule,
    StandardEvChargesRoutingModule
  ],
  declarations: [
    StandardEvChargesComponent,
    PerApplicantChargesModalComponent,
  ],
})
export class StandardEvChargesModule {
}
