import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-payment-failed-modal',
  templateUrl: './payment-failed-modal.component.html',
})

export class PaymentFailedModalComponent {
  @Input() errorContent = '';

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }
}
