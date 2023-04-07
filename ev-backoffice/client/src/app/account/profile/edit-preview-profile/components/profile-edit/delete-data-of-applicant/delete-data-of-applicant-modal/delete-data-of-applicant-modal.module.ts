import { NgModule } from '@angular/core';

import { SharedModule } from '../../../../../../../shared/shared.module';
import { ModalHeaderModule } from '../../../../../../../components/modal-header/modal-header.module';

import { DeleteDataOfApplicantModalComponent } from './delete-data-of-applicant-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    DeleteDataOfApplicantModalComponent,
  ],
  exports: [
    DeleteDataOfApplicantModalComponent,
  ],
  entryComponents: [
    DeleteDataOfApplicantModalComponent,
  ]
})

export class DeleteDataOfApplicantModalModule {
}
