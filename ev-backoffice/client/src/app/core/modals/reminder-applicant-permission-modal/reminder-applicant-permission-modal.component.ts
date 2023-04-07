import { Component } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-reminder-applicant-permission-modal',
  templateUrl: 'reminder-applicant-permission-modal.component.html',
})
export class ReminderApplicantPermissionModalComponent {

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }
}
