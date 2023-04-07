import { Component, Input } from '@angular/core';
import { ModalDismissReasons, NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-modal-header',
  templateUrl: 'modal-header.component.html',
})
export class ModalHeaderComponent {

  @Input() title: string;
  @Input() showCloseIcon = true;

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  onCloseIconClick() {
    this.activeModal.dismiss(ModalDismissReasons.ESC);
  }
}
