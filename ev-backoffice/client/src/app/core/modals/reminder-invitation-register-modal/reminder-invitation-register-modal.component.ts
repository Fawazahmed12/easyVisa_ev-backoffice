import { Component } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-reminder-invitation-register-modal',
  templateUrl: 'reminder-invitation-register-modal.component.html',
})
export class ReminderInvitationRegisterModalComponent {

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }
}
