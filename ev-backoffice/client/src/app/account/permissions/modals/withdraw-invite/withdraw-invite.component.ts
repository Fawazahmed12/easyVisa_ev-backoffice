import { Component, Input } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { OrganizationEmployee } from '../../models/organization-employee.model';


@Component({
  selector: 'app-withdraw-invite',
  templateUrl: './withdraw-invite.component.html',
})

export class WithdrawInviteComponent {
  @Input() employee: OrganizationEmployee;

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  closeModal() {
    this.activeModal.close();
  }

  cancelModal() {
    this.activeModal.dismiss();
  }
}
