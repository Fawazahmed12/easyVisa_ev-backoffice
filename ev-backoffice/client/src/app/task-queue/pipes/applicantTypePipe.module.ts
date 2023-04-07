import { NgModule } from '@angular/core';

import { ApplicantTypePipe } from './applicantType.pipe';

@NgModule({
  declarations: [
    ApplicantTypePipe,
  ],
  exports: [
    ApplicantTypePipe
  ]
})
export class ApplicantTypePipeModule { }
