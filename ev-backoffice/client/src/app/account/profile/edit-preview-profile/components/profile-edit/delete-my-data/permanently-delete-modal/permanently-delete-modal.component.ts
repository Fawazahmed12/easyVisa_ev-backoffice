import { Component } from '@angular/core';
import { FormControl } from '@angular/forms';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';


@Component({
  selector: 'app-permanently-delete-modal',
  templateUrl: './permanently-delete-modal.component.html',
})

export class PermanentlyDeleteModalComponent {
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
