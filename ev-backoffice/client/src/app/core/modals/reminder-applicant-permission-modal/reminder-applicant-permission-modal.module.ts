import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

import { ReminderApplicantPermissionModalComponent } from './reminder-applicant-permission-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    ReminderApplicantPermissionModalComponent,
  ],
  exports: [
    ReminderApplicantPermissionModalComponent,
  ],
  entryComponents: [
    ReminderApplicantPermissionModalComponent,
  ],
})

export class ReminderApplicantPermissionModalModule {
}
