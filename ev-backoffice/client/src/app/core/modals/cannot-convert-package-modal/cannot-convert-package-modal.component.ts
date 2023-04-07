import { Component, Input, OnInit } from '@angular/core';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { Package } from '../../models/package/package.model';
import { ApplicantType } from '../../models/applicantType.enum';
import { Applicant } from '../../models/applicant.model';

@Component({
  selector: 'app-cannot-convert-package-modal',
  templateUrl: 'cannot-convert-package-modal.component.html',
})
export class CannotConvertPackageModalComponent implements OnInit {
  @Input() item: Package;

  petitionerProfile: Applicant;

  constructor(
    private activeModal: NgbActiveModal,
  ) {
  }

  ngOnInit() {
    this.petitionerProfile = !!this.item ?
      this.item.applicants.find(
      (applicant) =>
        applicant.applicantType === ApplicantType.PETITIONER
        || (this.item.applicants.length === 1 && applicant.applicantType === ApplicantType.BENEFICIARY)).profile
      : null;
  }

  modalDismiss() {
    this.activeModal.dismiss();
  }
}
