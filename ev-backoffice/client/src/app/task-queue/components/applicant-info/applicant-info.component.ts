import { Component, EventEmitter, Input, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';

import { Observable, Subject } from 'rxjs';
import { map, pluck } from 'rxjs/operators';

import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

import { RequestState } from '../../../core/ngrx/utils';
import { ModalService, PackagesService } from '../../../core/services';
import { ApplicantType } from '../../../core/models/applicantType.enum';
import { PackageStatus } from '../../../core/models/package/package-status.enum';
import { benefitCategories } from '../../../core/models/benefit-categories';

import { CreateApplicantFormGroupService } from '../../package/services';

@Component({
  selector: 'app-applicant-info',
  templateUrl: './applicant-info.component.html',
})
export class ApplicantInfoComponent implements OnInit {
  @Input() applicantTypeFormControl: FormControl = new FormControl();
  @Input() benefitCategoryFormControl: FormControl = new FormControl();
  @Input() inviteApplicantFormControl: FormControl = new FormControl();
  @Input() applicantFee: number | string;
  @Input() showFee = false;
  @Input() showEmail = false;
  @Input() profileFormGroup: FormGroup;
  @Input() showResendButton = false;
  @Input() showInviteResendCheckbox = false;
  @Input() index: number;
  @Input() minAge: NgbDateStruct;
  @Input() maxAge: NgbDateStruct;
  @Input() showToolTips = false;
  @Output() resendWelcome: EventEmitter<string> = new EventEmitter();
  @Output() resendInvite: EventEmitter<string> = new EventEmitter();
  @ViewChild('petitionerTips', { static: true }) petitionerTips;
  @ViewChild('beneficiaryTips', { static: true }) beneficiaryTips;
  @ViewChild('derivativeBeneficiaryTips', { static: true }) derivativeBeneficiaryTips;
  @ViewChild('selfPackageBeneficiaryTips', { static: true }) selfPackageBeneficiaryTips;

  applicantInvitePostRequest$: Observable<RequestState<{ message: string }>>;
  isValidPackageStatus$: Observable<boolean>;
  packageStatus$: Observable<PackageStatus>;
  resendTitle$: Observable<string>;
  resendWelcomeTitle$: Observable<string>;
  submittedFormSubject$: Subject<boolean>;

  benefitCategories = benefitCategories;
  PackageStatus = PackageStatus;

  get homeAddressFormGroup() {
    return this.profileFormGroup.get('homeAddress');
  }

  get emailFormControl() {
    return this.profileFormGroup.get('email');
  }

  get idFormControl() {
    return this.profileFormGroup.get('id');
  }

  get EVIdFormControl() {
    return this.profileFormGroup.get('easyVisaId');
  }

  get firstFormControl() {
    return this.profileFormGroup.get('firstName');
  }

  get middleFormControl() {
    return this.profileFormGroup.get('middleName');
  }

  get lastFormControl() {
    return this.profileFormGroup.get('lastName');
  }

  get dateOfBirthFormControl() {
    return this.profileFormGroup.get('dateOfBirth');
  }

  get mobileNumberFormControl() {
    return this.profileFormGroup.get('mobileNumber');
  }

  get homeNumberFormControl() {
    return this.profileFormGroup.get('homeNumber');
  }

  get workNumberFormControl() {
    return this.profileFormGroup.get('workNumber');
  }

  constructor(
    private modalService: ModalService,
    private packageService: PackagesService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
    private packagesService: PackagesService,
  ) {

  }

  ngOnInit() {
    this.submittedFormSubject$ = this.createApplicantFormGroupService.submittedSubject$;
    this.applicantInvitePostRequest$ = this.packageService.applicantInvitePostRequest$;
    this.isValidPackageStatus$ = this.packagesService.package$.pipe(
      map((item) => item.status === PackageStatus.OPEN || item.status === PackageStatus.LEAD)
    );
    this.packageStatus$ = this.packagesService.package$.pipe(
      pluck('status')
    );

    this.resendTitle$ = this.packagesService.package$.pipe(
      pluck('status'),
      map((packageStatus) => {
        switch (packageStatus) {
          case PackageStatus.LEAD: {
            return 'TEMPLATE.TASK_QUEUE.APPLICANT.RESEND_WELCOME_TITLE';
          }
          case PackageStatus.OPEN: {
            return 'TEMPLATE.TASK_QUEUE.APPLICANT.RESEND_INVITATION_TITLE';
          }
          default: {
            return '';
          }
        }
        }
      )
    );

    this.resendWelcomeTitle$ = this.packagesService.package$.pipe(
      pluck('welcomeEmailId'),
      map((welcomeEmailId) => !!welcomeEmailId ? 'TEMPLATE.TASK_QUEUE.APPLICANT.RESEND_WELCOME_EMAIL'
        : 'TEMPLATE.TASK_QUEUE.APPLICANT.SEND_WELCOME_EMAIL')
    );
  }

  resendWelcomeApplicant() {
    this.resendWelcome.emit(this.idFormControl.value);
  }

  resendInviteApplicant() {
    this.resendInvite.emit(this.idFormControl.value);
  }

  getTemplate(applicantType, index) {
    if (index === 0 && ApplicantType.BENEFICIARY) {
      return this.selfPackageBeneficiaryTips;
    }
    switch (applicantType) {
      case ApplicantType.PRINCIPAL_BENEFICIARY:
      case ApplicantType.BENEFICIARY: {
        return this.beneficiaryTips;
      }
      case ApplicantType.DERIVATIVE_BENEFICIARY: {
        return this.derivativeBeneficiaryTips;
      }
      case ApplicantType.PETITIONER: {
        return this.petitionerTips;
      }
    }
  }
}
