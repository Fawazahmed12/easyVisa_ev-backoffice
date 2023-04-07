import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ApplicantTypePipeModule } from '../../pipes/applicantTypePipe.module';

import { ApplicantsListComponent } from './applicants-list.component';

@NgModule({
  imports: [
    SharedModule,
    ApplicantTypePipeModule,
  ],
  declarations: [
    ApplicantsListComponent,
  ],
  exports: [
    ApplicantsListComponent,
  ]
})
export class ApplicantsListModule {
}
