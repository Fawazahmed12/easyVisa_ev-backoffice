import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-delete-employee-modal',
  templateUrl: './delete-employee-modal.component.html',
  styleUrls: ['./delete-employee-modal.component.scss'],
})

export class DeleteEmployeeModalComponent {
  confirmFormControl = new FormControl(false);

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  closeModal() {
    this.activeModal.dismiss();
  }

  confirmModal() {
    this.activeModal.close(this.confirmFormControl.value);
  }
}
