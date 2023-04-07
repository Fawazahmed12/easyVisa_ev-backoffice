import { Component, Input, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { ModalDismissReasons, NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { Observable, of } from 'rxjs';

import { PackageStatus } from '../../../../../core/models/package/package-status.enum';
import { User } from '../../../../../core/models/user.model';
import { UserService } from '../../../../../core/services';
import { Package } from '../../../../../core/models/package/package.model';
import { ApplicantType } from '../../../../../core/models/applicantType.enum';

import { UpdatePackageStatusModalComponent } from '../update-package-status-modal/update-package-status-modal.component';

@Component({
  selector: 'app-change-case-status-modal',
  templateUrl: 'change-case-status-modal.component.html',
  styles: [
      `.change-status-applicants {
      width: 100%;
      border: 1px solid lightgray;
      min-height: 120px;
      max-height: 120px;
      overflow-y: auto;
    }
    .font-16{
      font-size: 16px;
    }`
  ]
})
export class ChangeCaseStatusModalComponent implements OnInit {
  @Input() item: Package;

  currentUser$: Observable<User> = this.userService.currentUser$;
  hideWarning$: Observable<boolean>;

  formGroup: FormGroup;

  statuses = [
    {
      label: 'TEMPLATE.TASK_QUEUE.CLIENTS.OPEN',
      value: PackageStatus.OPEN,
    },
    {
      label: 'TEMPLATE.TASK_QUEUE.CLIENTS.BLOCKED',
      value: PackageStatus.BLOCKED,
    },
    {
      label: 'TEMPLATE.TASK_QUEUE.CLIENTS.CLOSED',
      value: PackageStatus.CLOSED,
    }
  ];

  get statusFormControl() {
    return this.formGroup.get('status');
  }

  constructor(
    private activeModal: NgbActiveModal,
    private ngbModal: NgbModal,
    private userService: UserService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
  ) {
  }

  ngOnInit() {
    this.formGroup = new FormGroup({
      status: new FormControl(this.item.status),
    });
    this.hideWarning$ = of(true);
  }

  disabledStatuses(status) {
    switch (this.item.status) {
      case PackageStatus.OPEN: {
        return status === PackageStatus.OPEN;
      }
      case PackageStatus.BLOCKED: {
        return status === PackageStatus.BLOCKED;
      }
      case PackageStatus.LEAD:
      case PackageStatus.CLOSED: {
        return status !== PackageStatus.OPEN;
      }
    }
  }

  getPackageApplicants() {
    const applicants = this.item.applicants || [];
    const packageApplicants = [];
    const petitioner = applicants.find((data) => data.applicantType === ApplicantType.PETITIONER);
    if (petitioner) {
      packageApplicants.push(this.getPackageApplicantData('Petitioner/Client', petitioner));
    }

    const beneficiary = applicants.find((data) => [ ApplicantType.BENEFICIARY, ApplicantType.PRINCIPAL_BENEFICIARY ].indexOf(data.applicantType) >= 0);
    packageApplicants.push(this.getPackageApplicantData('Beneficiary', beneficiary));

    const derivativeBenficiaryList = applicants.filter((data) => data.applicantType === ApplicantType.DERIVATIVE_BENEFICIARY);
    derivativeBenficiaryList.forEach((data, index) => {
      const applicantType = `Derivative Beneficiary ${index + 1}`;
      packageApplicants.push(this.getPackageApplicantData(applicantType, data));
    });
    return packageApplicants;
  }

  private getPackageApplicantData(applicantType, applicant) {
    const profile = applicant.profile;
    const applicantName = `${profile.firstName} ${profile.lastName}`;
    return { applicantType, applicantName };
  }

  onStatusSelection() {
    const chosenStatus = this.formGroup.value.status;
    if (chosenStatus === PackageStatus.OPEN && this.item.status === PackageStatus.LEAD) {
      this.hideWarning$ = of(false);
    } else {
      this.hideWarning$ = of(true);
    }
  }

  onSubmit() {
    const chosenStatus = this.formGroup.value.status;

    if (this.item.status === PackageStatus.LEAD && chosenStatus === PackageStatus.OPEN) {
      this.router.navigate(['task-queue', 'clients', this.item.id, 'invitation-to-register'], {relativeTo: this.activatedRoute});
    } else  if (chosenStatus !== this.item.status) {
      this.openCaseModal(UpdatePackageStatusModalComponent);
    }
    this.activeModal.dismiss();
  }

  openCaseModal(component) {
    const modalRef = this.ngbModal.open(component, {
      windowClass: 'custom-modal-lg',
      centered: true,
    });
    modalRef.componentInstance.item = this.item;
    modalRef.componentInstance.selectedStatus = this.formGroup.value.status;
  }

  closeModal() {
    this.activeModal.close(ModalDismissReasons.ESC);
  }
}
