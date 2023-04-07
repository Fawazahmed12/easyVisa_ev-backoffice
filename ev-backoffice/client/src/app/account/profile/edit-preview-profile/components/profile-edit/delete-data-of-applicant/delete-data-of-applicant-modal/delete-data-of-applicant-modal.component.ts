import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-delete-data-of-applicant-modal',
  templateUrl: './delete-data-of-applicant-modal.component.html',
})

export class DeleteDataOfApplicantModalComponent {
  confirmFormControl = new FormControl(false);

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  closeModal() {
    this.activeModal.dismiss();
  }

  confirmModal() {
    this.activeModal.close();
  }
}
