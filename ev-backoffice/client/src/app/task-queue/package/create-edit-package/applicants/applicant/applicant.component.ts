import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormControl, FormGroup, } from '@angular/forms';

import { EMPTY, Observable, ReplaySubject, Subject } from 'rxjs';
import { catchError, distinctUntilChanged, filter, map, startWith, switchMap, withLatestFrom } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

import { ConfirmButtonType } from '../../../../../core/modals/confirm-modal/confirm-modal.component';
import { ApplicantType } from '../../../../../core/models/applicantType.enum';
import { Applicant } from '../../../../../core/models/applicant.model';
import { OfficeAddress } from '../../../../../core/models/officeAddress.model';
import { ConfigDataService, ModalService, PackagesService } from '../../../../../core/services';

import { CreateApplicantFormGroupService } from '../../../services';
import { PetitionerStatus } from '../../../../../core/models/petitioner-status.enum';


@Component({
  selector: 'app-applicant',
  templateUrl: './applicant.component.html',
  styleUrls: ['./applicant.component.scss'],
})
@DestroySubscribers()
export class ApplicantComponent implements AddSubscribers, OnDestroy, OnInit {
  @Input() packageTypeFormControl: FormControl;
  @Input() applicantFormGroup: FormGroup;
  @Input() applicantType: ApplicantType = ApplicantType.BENEFICIARY;
  @Input() index = null;
  @Input() minAge: NgbDateStruct;
  @Input() maxAge: NgbDateStruct;

  @ViewChild('deleteBeneficiaryModal', {static: true}) deleteBeneficiaryModal;
  @ViewChild('deleteDerivativeBeneficiaryModal', {static: true}) deleteDerivativeBeneficiaryModal;

  private removeBeneficiarySubject$: Subject<ApplicantType> = new Subject<ApplicantType>();
  private removeDerivativeBeneficiarySubject$: Subject<ApplicantType> = new Subject<ApplicantType>();
  private onEditMode$: ReplaySubject<boolean> = new ReplaySubject<boolean>(1);
  private onApplicantFill$: Subject<boolean> = new Subject<boolean>();
  private resendWelcomeSubject$: Subject<string> = new Subject<string>();
  private resendInviteSubject$: Subject<string> = new Subject<string>();

  buttons = [
    {
      label: 'FORM.BUTTON.CANCEL',
      type: ConfirmButtonType.Dismiss,
      className: 'btn btn-primary mr-2',
    },
    {
      label: 'FORM.BUTTON.DELETE',
      type: ConfirmButtonType.Close,
      className: 'btn btn-primary ml-2',
    },
  ];

  private subscribers: any = {};

  warningMessage$: Observable<string>;

  constructor(
    private modalService: ModalService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
    private packageService: PackagesService,
    private configDataService: ConfigDataService,
  ) {
  }

  get petitionerBgColor() {
    if (this.index === 0) {
      return 'bg-f0f6fa';
    } else {
      switch (this.applicantFormGroup.value.applicantType) {
        case ApplicantType.BENEFICIARY: {
          return 'bg-e8e8e8';
        }
        case ApplicantType.PRINCIPAL_BENEFICIARY: {
          return 'bg-e8e8e8';
        }
        case ApplicantType.DERIVATIVE_BENEFICIARY: {
          return 'bg-f5f5f5';
        }
        default: {
          return '';
        }
      }
    }
  }

  get formGroup() {
    return this.createApplicantFormGroupService.formGroup;
  }

  get applicantsFormArray() {
    return this.createApplicantFormGroupService.formGroup.get('applicants') as FormArray;
  }

  get profileFormGroup() {
    return this.applicantFormGroup.get('profile') as FormGroup;
  }

  get EVIdFormControl() {
    return this.applicantFormGroup.get('profile').get('easyVisaId');
  }

  get citizenshipStatusFormControl() {
    return this.applicantFormGroup.get('citizenshipStatus');
  }

  get applicantTypeFormControl() {
    return this.applicantFormGroup.get('applicantType');
  }

  get emailFormControl() {
    return this.applicantFormGroup.get('profile').get('email');
  }

  get inviteApplicantFormControl() {
    return this.applicantFormGroup.get('inviteApplicant');
  }

  get benefitCategoryFormControl() {
    return this.applicantFormGroup.get('benefitCategory');
  }

  get feeFormControl() {
    return this.applicantFormGroup.get('fee');
  }

  get isEmailVerifiedFormControl() {
    return this.applicantFormGroup.get('profile').get('isEmailVerified');
  }

  get optInFromControl() {
    return this.applicantFormGroup.get('optIn');
  }

  get registerFromControl() {
    return this.applicantFormGroup.get('register');
  }

  get inOpenPackageFromControl() {
    return this.applicantFormGroup.get('inOpenPackage');
  }

  get inBlockedPackageFromControl() {
    return this.applicantFormGroup.get('inBlockedPackage');
  }

  ngOnInit() {
    this.onEditMode$.next(!!this.EVIdFormControl.value);

    this.warningMessage$ = this.applicantsFormArray.valueChanges.pipe(
      startWith(this.applicantsFormArray.value),
      map((applicants) => {
        const [petitioner, ...rest] = applicants;
        const applicantTypeValue = this.applicantTypeFormControl.value;
        if (petitioner?.citizenshipStatus === PetitionerStatus.ALIEN) {
          return 'TEMPLATE.TASK_QUEUE.APPLICANT.ADD_ANOTHER_APPLICANT_WARNING';
        }
        const isBeneficiary = !!applicants.find(applicant => applicant.applicantType === ApplicantType.BENEFICIARY);
        const hasDerivatives = !!applicants.find(applicant => applicant.applicantType === ApplicantType.DERIVATIVE_BENEFICIARY);
        if (isBeneficiary && hasDerivatives && applicantTypeValue === ApplicantType.DERIVATIVE_BENEFICIARY) {
          return 'TEMPLATE.TASK_QUEUE.APPLICANT.ADD_ANOTHER_DERIVATIVE_WARNING';
        }
      }),
      distinctUntilChanged(),
    );
  }

  addSubscribers() {
    this.subscribers.onApplicantFillSubscription = this.onApplicantFill$
    .subscribe(() => {
      this.emailFormControl.enable({emitEvent: false});
      this.inviteApplicantFormControl.enable({emitEvent: false});
    });

    this.subscribers.disablePetitionerSpecificFields = this.onEditMode$.pipe(
      filter((isEditMode) => isEditMode && this.applicantType === ApplicantType.PETITIONER),
    ).subscribe(() => {
      this.citizenshipStatusFormControl.disable({emitEvent: false});
    });

    this.subscribers.deleteBeneficiarySubscription = this.removeBeneficiarySubject$.pipe(
      switchMap(() => this.removeBeneficiaryApplicantModal())
    ).subscribe(() => {
      this.applicantsFormArray.value.forEach((applicant) => {
        if (applicant.applicantType !== ApplicantType.PETITIONER) {
          this.applicantsFormArray.removeAt(this.index);
        }
      });
    });

    this.subscribers.deleteDerivativeBeneficiarySubscription = this.removeDerivativeBeneficiarySubject$.pipe(
      switchMap(() => this.removeDerivativeBeneficiaryModal())
    ).subscribe(() => {
      this.applicantsFormArray.removeAt(this.index);
    });

    this.subscribers.resendWelcomeSubscription = this.resendWelcomeSubject$
    .subscribe(() => this.packageService.reSendPackageEmail(this.formGroup.value.id));

    this.subscribers.resendInviteSubscription = this.resendInviteSubject$
    .subscribe((applicantId) => this.packageService.resendWelcomeApplicant(this.formGroup.value.id, applicantId));

    this.subscribers.benefitCategoryFormControlSubscription = this.citizenshipStatusFormControl.valueChanges.pipe(
      filter(() => this.index === 0),
    ).subscribe(() => this.benefitCategoryFormControl.reset(''));

    this.subscribers.benefitCategoryFormControlSubscription = this.benefitCategoryFormControl.valueChanges.pipe(
      filter(() => this.index === 0),
      filter(() => {
          const applicantTypeValue = this.applicantTypeFormControl.value;
          return [ApplicantType.PETITIONER, ApplicantType.BENEFICIARY].includes(applicantTypeValue);
        }
      ),
    ).subscribe((benefitCategory) => {
      const isPetitioner = this.applicantTypeFormControl.value === ApplicantType.PETITIONER;
      if (isPetitioner && !!benefitCategory) {
        this.applicantTypeFormControl.patchValue(ApplicantType.BENEFICIARY);
      } else if (benefitCategory === null) {
        this.applicantTypeFormControl.patchValue(ApplicantType.PETITIONER);
      }
    });

    this.subscribers.benefitCategoryFormControlSubscription = this.benefitCategoryFormControl.valueChanges.pipe(
      filter((benefitCategory) => !!benefitCategory && this.index === 1),
      filter(() => {
        const applicantTypeValue = this.applicantTypeFormControl.value;
        return [ApplicantType.BENEFICIARY, ApplicantType.PRINCIPAL_BENEFICIARY].includes(applicantTypeValue);
      }),
      withLatestFrom(this.configDataService.withDerivativesBenefitCategories$),
    ).subscribe(([benefitCategory, withDerivativesBenefitCategories]) => {
      const isPrincipalBeneficiary = withDerivativesBenefitCategories.some(category => category.value === benefitCategory);
      this.applicantTypeFormControl.patchValue(
        isPrincipalBeneficiary ?
          ApplicantType.PRINCIPAL_BENEFICIARY :
          ApplicantType.BENEFICIARY
      );
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  removeApplicant(applicantType) {
    if (applicantType === ApplicantType.BENEFICIARY || applicantType === ApplicantType.PRINCIPAL_BENEFICIARY) {
      this.removeBeneficiarySubject$.next(applicantType);
    } else {
      this.removeDerivativeBeneficiarySubject$.next(applicantType);
    }
  }

  removeBeneficiaryApplicantModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.TASK_QUEUE.PACKAGE.DELETE_APPLICANT_MODAL.TITLE',
      body: this.deleteBeneficiaryModal,
      buttons: this.buttons,
      size: 'lg',
      centered: true,
    })
    .pipe(
      catchError(() => EMPTY)
    );
  }

  removeDerivativeBeneficiaryModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.TASK_QUEUE.PACKAGE.DELETE_DERIVATIVE_APPLICANT_MODAL.TITLE',
      body: this.deleteDerivativeBeneficiaryModal,
      buttons: this.buttons,
      size: 'lg',
      centered: true,
    })
    .pipe(
      catchError(() => EMPTY)
    );
  }

  fillApplicantData(data) {
    if (data) {
      const applicant: Applicant = {
        ...data,
        homeAddress: data.homeAddress || {} as OfficeAddress,
      };
      this.profileFormGroup.patchValue(applicant);
      this.profileFormGroup.disable({emitEvent: false});
      this.isEmailVerifiedFormControl.enable({emitEvent: false});
    } else {
      this.profileFormGroup.enable({emitEvent: false});
      const profileData = this.profileFormGroup.getRawValue();
      const profileId = profileData.easyVisaId ? profileData.id : null;
      this.profileFormGroup.patchValue({id: profileId});
    }
    this.onApplicantFill$.next(true);
  }

  resendWelcomeApplicant(applicantId) {
    this.resendWelcomeSubject$.next(applicantId);
  }

  resendInviteApplicant(applicantId) {
    this.resendInviteSubject$.next(applicantId);
  }
}
