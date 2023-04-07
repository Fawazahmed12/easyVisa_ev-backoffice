import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../shared/shared.module';

import { ApplicantsComponent } from './applicants.component';
import { ApplicantModule } from './applicant/applicant.module';
import { AdditionalApplicantModalComponent } from './modals/additional-applicant-modal/additional-applicant-modal.component';

@NgModule({
  imports: [
    SharedModule,
    ApplicantModule,
  ],
  declarations: [
    ApplicantsComponent,
    AdditionalApplicantModalComponent,
  ],
  exports: [
    ApplicantsComponent,
  ]
})
export class ApplicantsModule {
}
