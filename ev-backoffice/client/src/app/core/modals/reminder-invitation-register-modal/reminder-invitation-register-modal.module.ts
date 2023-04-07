import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { ModalHeaderModule } from '../../../components/modal-header/modal-header.module';

import { ReminderInvitationRegisterModalComponent } from './reminder-invitation-register-modal.component';


@NgModule({
  imports: [
    SharedModule,
    ModalHeaderModule,
  ],
  declarations: [
    ReminderInvitationRegisterModalComponent,
  ],
  exports: [
    ReminderInvitationRegisterModalComponent,
  ],
  entryComponents: [
    ReminderInvitationRegisterModalComponent,
  ],
})

export class ReminderInvitationRegisterModalModule {
}
