import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../shared/shared.module';

import { DeleteDataOfApplicantComponent } from './delete-data-of-applicant.component';
import { DeleteDataOfApplicantModalModule } from './delete-data-of-applicant-modal/delete-data-of-applicant-modal.module';
import { SpinnerModule } from '../../../../../../components/spinner/spinner.module';

@NgModule({
  imports: [
    SharedModule,
    DeleteDataOfApplicantModalModule,
    SpinnerModule,
  ],
  declarations: [
    DeleteDataOfApplicantComponent,
  ],
  exports: [
    DeleteDataOfApplicantComponent,
  ]
})

export class DeleteDataOfApplicantModule {
}
