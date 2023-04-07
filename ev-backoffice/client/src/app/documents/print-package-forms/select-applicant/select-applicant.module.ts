import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ApplicantTypePipeModule } from '../../../task-queue/pipes/applicantTypePipe.module';

import { SelectApplicantComponent } from './select-applicant.component';


@NgModule({
  imports: [
    SharedModule,
    ApplicantTypePipeModule,
  ],
  declarations: [
    SelectApplicantComponent,
  ],
  exports: [
    SelectApplicantComponent
  ],
  entryComponents: [
    SelectApplicantComponent
  ]
})
export class SelectApplicantModule { }
