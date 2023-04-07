import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-no-results-modal',
  templateUrl: './no-results-modal.component.html',
})

export class NoResultsModalComponent {

  @Input() noResultModalDescription = '';

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }
}
