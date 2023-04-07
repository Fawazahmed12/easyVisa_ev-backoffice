import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-members-of-blocked-or-open-package-modal',
  templateUrl: './members-of-blocked-or-open-package-modal.component.html',
})

export class MembersOfBlockedOrOpenPackageModalComponent {
  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  closeModal() {
    this.activeModal.dismiss();
  }
}
