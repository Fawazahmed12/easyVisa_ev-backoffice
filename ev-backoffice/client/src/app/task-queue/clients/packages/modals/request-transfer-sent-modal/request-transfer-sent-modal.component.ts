import { Component, Input } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { Profile } from '../../../../../core/models/profile.model';


@Component({
  selector: 'app-request-transfer-sent-modal',
  templateUrl: './request-transfer-sent-modal.component.html',
})

export class RequestTransferSentModalComponent {
  @Input() request: Profile;

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  closeModal() {
    this.activeModal.close();
  }
}
