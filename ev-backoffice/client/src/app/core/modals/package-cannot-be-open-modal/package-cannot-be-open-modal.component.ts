import { Component, Input, OnInit } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { PackageApplicant } from '../../models/package/package-applicant.model';

@Component({
  selector: 'app-package-cannot-be-open-modal',
  templateUrl: 'package-cannot-be-open-modal.component.html',
})
export class PackageCannotBeOpenModalComponent implements OnInit {
  @Input() pendingOptApplicants: PackageApplicant[];
  @Input() denyOptApplicants: PackageApplicant[];

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  ngOnInit() {
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }
}
