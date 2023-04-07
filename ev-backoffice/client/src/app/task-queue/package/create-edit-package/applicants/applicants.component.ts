import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormArray, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { catchError, distinctUntilChanged, filter, map, startWith, switchMap, switchMapTo, take, withLatestFrom } from 'rxjs/operators';
import { combineLatest, EMPTY, of, Observable, Subject } from 'rxjs';

import { NgbDateStruct } from '@ng-bootstrap/ng-bootstrap';

import { ApplicantType } from '../../../../core/models/applicantType.enum';
import { ConfirmButtonType } from '../../../../core/modals/confirm-modal/confirm-modal.component';
import { ConfigDataService, ModalService, PackagesService } from '../../../../core/services';
import { Package } from '../../../../core/models/package/package.model';
import { PackageStatus } from '../../../../core/models/package/package-status.enum';
import { getOnlyDate } from '../../../../shared/utils/get-only-date';

import { FeeDetails } from '../../../../core/models/fee-details.model';
import { PetitionerStatus } from '../../../../core/models/petitioner-status.enum';
import { BenefitCategories } from '../../../../core/models/benefit-categories.enum';

import { CreateApplicantFormGroupService } from '../../services';

@Component({
  selector: 'app-applicants',
  templateUrl: './applicants.component.html',
})
@DestroySubscribers()
export class ApplicantsComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() packageTypeFormControl: FormControl;
  @Input() isReadOnlyPackage: Boolean = false;
  @ViewChild('additionalApplicantModal', {static: true}) additionalApplicantModal;

  feeDetails$: Observable<FeeDetails>;
  currentPackage$: Observable<Package>;
  applicantAgeConfig: {
    beneficiaryMinAge: NgbDateStruct | Date;
    petitionerMinAge: NgbDateStruct | Date;
    applicantMaxAge: NgbDateStruct | Date;
  };
  canAddApplicant$: Observable<boolean>;
  availableToAddDerivativeBeneficiary$: Observable<boolean>;

  private anotherApplicantSubject$ = new Subject<void>();
  private subscribers: any = {};

  constructor(
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
    private packagesService: PackagesService,
    private modalService: ModalService,
    private activatedRoute: ActivatedRoute,
    private configDataService: ConfigDataService,
  ) {

  }

  get applicantsFormArray() {
    return this.createApplicantFormGroupService.formGroup.get('applicants') as FormArray;
  }

  ngOnInit() {
    this.applicantAgeConfig = this.getMinMaxAges();
    this.currentPackage$ = this.packagesService.package$;
    this.feeDetails$ = this.configDataService.feeDetails$;
    this.canAddApplicant$ = this.applicantsFormArray.valueChanges.pipe(
      startWith(this.applicantsFormArray.value),
      map(applicants => applicants.length === 1
        && !!applicants.find(applicant => applicant.applicantType === ApplicantType.BENEFICIARY)
      ),
      distinctUntilChanged(),
    );

    this.availableToAddDerivativeBeneficiary$ = this.applicantsFormArray.valueChanges.pipe(
      startWith(this.applicantsFormArray.value),
      map(applicants => {
          const [firstApplicant, ...rest] = applicants;
          const validLegalStatus = [PetitionerStatus.U_S_CITIZEN, PetitionerStatus.LPR];
          const isValidPetitionerStatus = validLegalStatus.includes(firstApplicant?.citizenshipStatus);

          const validBenefitCategories = [
            BenefitCategories.K1K3,
            BenefitCategories.F1_A,
            BenefitCategories.F2_A,
            BenefitCategories.F3_A,
            BenefitCategories.F4_A,
          ];
          const principalBeneficiary = applicants.find(applicant => applicant.applicantType === ApplicantType.PRINCIPAL_BENEFICIARY);
          const principalBeneficiaryCategory = !!principalBeneficiary && principalBeneficiary.benefitCategory;
          const isValidBenefitCategories = validBenefitCategories.some(category => category === principalBeneficiaryCategory);
          return applicants.length === 1 ? isValidPetitionerStatus : isValidPetitionerStatus && isValidBenefitCategories;
        }
      ),
      distinctUntilChanged(),
    );
  }

  addSubscribers() {
    this.subscribers.currentPackageSubscription = this.anotherApplicantSubject$.pipe(
      switchMapTo(this.currentPackage$.pipe(take(1))),
      withLatestFrom(this.activatedRoute.params),
      switchMap(([currentPackage, routeData]) => {
        if (routeData.id && currentPackage.status !== PackageStatus.LEAD) {
          return this.openAdditionalApplicantModal();
        } else {
          return of(null);
        }
      }))
    .subscribe(() => this.addApplicant());

    this.subscribers.currentPackageSubscription = combineLatest([
      this.applicantsFormArray.valueChanges,
      this.packagesService.activePackage$
    ]).pipe(
      filter(([, activePackage]) => !activePackage),
      map(([applicants, ]) => {
        const usualPetitioner = applicants.find((applicant) =>
          applicant.benefitCategory === null && applicant.applicantType === ApplicantType.PETITIONER && applicants.length === 1);
        return usualPetitioner || null;
      }),
      filter((usualPetitioner) => !!usualPetitioner),
      take(1),
    )
    .subscribe(() => this.addAnotherApplicant());
  }

  ngOnDestroy() {
    this.packagesService.addApplicantBtnClickedSubject$.next(false);
  }

  addAnotherApplicant() {
    this.anotherApplicantSubject$.next();
    this.packagesService.addApplicantBtnClickedSubject$.next(true);
  }

  addApplicant() {
    const beneficiaryExisted = this.applicantsFormArray.value.find((applicant) =>
      applicant.applicantType === ApplicantType.BENEFICIARY ||
      applicant.applicantType === ApplicantType.PRINCIPAL_BENEFICIARY
    );

    const addedApplicantType = beneficiaryExisted ? ApplicantType.DERIVATIVE_BENEFICIARY : ApplicantType.BENEFICIARY;
    this.applicantsFormArray.push(this.createApplicantFormGroupService.createBeneficiaryFormGroup(addedApplicantType));
  }

  openAdditionalApplicantModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.TASK_QUEUE.PACKAGE.ADDITIONAL_APPLICANT.TITLE',
      body: this.additionalApplicantModal,
      buttons,
      size: 'lg',
      centered: true,
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  getMinMaxAges() {
    const applicantAges = this.createApplicantFormGroupService.getRequiredAges();
    return {
      beneficiaryMinAge: getOnlyDate(applicantAges.beneficiaryMinAge),
      petitionerMinAge: getOnlyDate(applicantAges.petitionerMinAge),
      applicantMaxAge: getOnlyDate(applicantAges.applicantMaxAge),
    };
  }
}
