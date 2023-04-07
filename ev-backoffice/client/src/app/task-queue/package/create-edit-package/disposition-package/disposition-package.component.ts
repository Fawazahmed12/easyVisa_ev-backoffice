import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormArray } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { combineLatest, EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, filter, map, startWith, switchMap, switchMapTo } from 'rxjs/operators';

import { PackageStatus } from '../../../../core/models/package/package-status.enum';
import { ConfirmButtonType } from '../../../../core/modals/confirm-modal/confirm-modal.component';
import { Package } from '../../../../core/models/package/package.model';
import { RequestState } from '../../../../core/ngrx/utils';
import { ConfigDataService, ModalService, OrganizationService, PackagesService, TaxesService } from '../../../../core/services';
import { FeeDetails } from '../../../../core/models/fee-details.model';
import { ApplicantType } from '../../../../core/models/applicantType.enum';
import { TaxTypes } from '../../../../core/models/tax-types.enum';

import { PackageType } from '../../../models/package-type.enum';

import { CreateApplicantFormGroupService } from '../../services';

@Component({
  selector: 'app-disposition-package',
  templateUrl: './disposition-package.component.html',
})
@DestroySubscribers()
export class DispositionPackageComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() packageTypeFormControl;
  @ViewChild('deleteNewPackageModal', { static: true }) deleteNewPackageModal;
  @ViewChild('cancelModificationsModal', { static: true }) cancelModificationsModal;
  @ViewChild('benefitCategoryConflictModal', { static: false }) benefitCategoryConflictModal;
  @ViewChild('informationMissingModal', { static: true }) informationMissingModal;
  @ViewChild('confirmNoConflictModal', { static: true }) confirmNoConflictModal;
  @ViewChild('membersOfBlockedOrOpenPackageModal', { static: false }) membersOfBlockedOrOpenPackageModal;
  @ViewChild('additionalFeeInfo', { static: true }) additionalFeeInfo;

  package$: Observable<Package>;
  feeDetails$: Observable<FeeDetails>;
  editedPackage$: Observable<Package>;
  isSaveButtonDisabled$: Observable<boolean>;
  isCreateButtonEnabled$: Observable<boolean>;
  postPackageRequest$: Observable<RequestState<Package>>;
  patchPackageRequest$: Observable<RequestState<Package>>;
  postPackageWelcomeEmailRequest$: Observable<RequestState<{ message: string }>>;
  addApplicantBtnClickedSubject$: Subject<boolean> = new Subject<boolean>();

  buttons = [
    {
      label: 'FORM.BUTTON.CANCEL',
      type: ConfirmButtonType.Dismiss,
      className: 'btn btn-primary mr-2 min-w-100',
    },
    {
      label: 'FORM.BUTTON.CONFIRM',
      type: ConfirmButtonType.Close,
      className: 'btn btn-primary mr-2 min-w-100',
    },
  ];

  okButton = {
    label: 'FORM.BUTTON.OK',
    type: ConfirmButtonType.Dismiss,
    className: 'btn btn-primary mr-2 min-w-100',
  };

  private deleteNewPackageSubject$: Subject<boolean> = new Subject<boolean>();
  private cancelModificationSubject$: Subject<boolean> = new Subject<boolean>();
  private saveChangesSubject$: Subject<boolean> = new Subject<boolean>();
  private reSendEmailSubject$: Subject<boolean> = new Subject<boolean>();
  private subscribers: any = {};

  PackageStatus = PackageStatus;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private modalService: ModalService,
    private configDataService: ConfigDataService,
    private packagesService: PackagesService,
    private organizationService: OrganizationService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
    private taxesService: TaxesService,
  ) {

  }

  ngOnInit() {
    this.addApplicantBtnClickedSubject$ = this.packagesService.addApplicantBtnClickedSubject$;
    this.feeDetails$ = this.configDataService.feeDetails$;
    this.package$ = this.packagesService.package$;
    this.editedPackage$ = this.route.params.pipe(
      filter(params => params.id),
      switchMapTo(this.package$),
      filter(value => !!value),
    );
    this.postPackageRequest$ = this.packagesService.postPackageRequest$;
    this.patchPackageRequest$ = this.packagesService.patchPackageRequest$;
    this.postPackageWelcomeEmailRequest$ = this.packagesService.postPackageWelcomeEmailRequest$;

    this.isSaveButtonDisabled$ = this.formGroup.valueChanges.pipe(
      startWith(this.formGroup.value),
    ).pipe(
      map(() => {
        // TODO data should be in the same format for comparing
        const { representativeId, ...valueToPatch } = this.formGroup.getRawValue();
        return !this.createApplicantFormGroupService.checkFormGroupChanges(valueToPatch);
      })
    );

    this.isCreateButtonEnabled$ = combineLatest([
      this.applicantsFormArray.valueChanges,
      this.packagesService.activePackage$
    ]).pipe(
      filter(([applicants, activePackage]) => !!applicants && activePackage === null),
      map(([applicants,]) => {
        const petitioner = applicants.find((applicant) => applicant.applicantType === ApplicantType.PETITIONER);
        const selfPetitioner = applicants.find((applicant) => applicant.applicantType === ApplicantType.BENEFICIARY);
        return petitioner ? petitioner.profile : selfPetitioner.profile;
      }),
      map(profile => profile?.email !== '' && profile.isEmailVerified)
    );
  }

  get formGroup() {
    return this.createApplicantFormGroupService.formGroup;
  }

  get applicantsFormArray() {
    return this.createApplicantFormGroupService.formGroup.get('applicants') as FormArray;
  }

  addSubscribers() {
    this.subscribers.deleteNewPackageSubscription = this.deleteNewPackageSubject$.pipe(
      switchMap(() => this.openDeleteNewPackageModal())
    )
      .subscribe(() => {
        this.createApplicantFormGroupService.resetFormGroup();
        this.packageTypeFormControl.reset();
        this.packageTypeFormControl.enable({ emitEvent: false });
      });

    this.subscribers.cancelModificationsSubscription = this.cancelModificationSubject$.pipe(
      switchMap(() => this.openCancelModificationsModal())
    )
      .subscribe(() => {
        this.createApplicantFormGroupService.resetFormGroup();
        this.packageTypeFormControl.enable();
        this.packagesService.removePackage();
        this.packagesService.removePackages();
        this.router.navigate(['task-queue', 'package', 'create']);
      });


    this.subscribers.saveChangesNewSubject = this.saveChangesSubject$.pipe(
      filter(() => this.packageTypeFormControl.value === PackageType.NEW),
      switchMapTo(this.organizationService.activeOrganizationId$),
      map(id => parseInt(id, 10)),
      switchMap(organizationId =>
        this.packagesService.createPackage(
          {
            ...this.formGroup.getRawValue(),
            organizationId
          }).pipe(
          catchError(() => EMPTY)
        )
      ),
    ).subscribe((currentPackage: { package: Package }) =>
      this.router.navigate(['task-queue', 'package', currentPackage.package.id, 'welcome-email'])
    );

    const validTaxEstimationStatusList: PackageStatus[] = [
      PackageStatus.OPEN,
      PackageStatus.BLOCKED
    ];
    this.subscribers.saveEditChangesSubject = combineLatest([
      this.saveChangesSubject$,
      this.package$
    ]).pipe(
      filter(([hasSaveChanges, aPackage]) => hasSaveChanges && this.packageTypeFormControl.value === PackageType.EDIT),
      switchMap(([hasSaveChanges, aPackage]) => validTaxEstimationStatusList.includes(aPackage.status) ?
        this.getTaxes(this.formGroup.getRawValue()).pipe(
          catchError(() => EMPTY)
        ) : of({ estimatedTax: {} })
      ),
      switchMap(({ estimatedTax }) =>
        estimatedTax.immediateCharge?.length || estimatedTax.laterCharge?.length ? this.openApplicantFeeInfoModal() : of(false)),
      switchMap(() => this.packagesService.updatePackage(
        this.formGroup.getRawValue()
      ).pipe(
        filter((data: { package: Package }) => data?.package.id === this.formGroup.getRawValue().id),
        catchError(() => EMPTY)
      ))
    ).subscribe((currentPackage: { package: Package }) => {
      this.saveChangesSubject$.next(false);
      this.router.navigate(['task-queue', 'package', currentPackage.package.id, 'updated-package-email']);
    });

    this.subscribers.resendEmail = this.reSendEmailSubject$
      .subscribe(() => this.packagesService.reSendPackageEmail(this.formGroup.value.id));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  openApplicantFeeInfoModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.TASK_QUEUE.ADDITIONAL_APPLICANT_FEE.TITLE',
      body: this.additionalFeeInfo,
      buttons: this.buttons,
      size: 'lg',
      centered: true,
    })
      .pipe(
        catchError(() => EMPTY)
      );
  }

  openDeleteNewPackageModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.TASK_QUEUE.PACKAGE.DELETE_NEW_PACKAGE_MODAL.TITLE',
      body: this.deleteNewPackageModal,
      buttons: this.buttons,
      centered: true,
    })
      .pipe(
        catchError(() => EMPTY)
      );
  }

  openCancelModificationsModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.TASK_QUEUE.PACKAGE.CANCEL_MODIFICATIONS_MODAL.TITLE',
      body: this.cancelModificationsModal,
      buttons: this.buttons,
      centered: true,
    })
      .pipe(
        catchError(() => EMPTY)
      );
  }

  openInformationMissingModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.TASK_QUEUE.PACKAGE.INFORMATION_MISSING_MODAL.TITLE',
      body: this.informationMissingModal,
      size: 'lg',
      buttons: [this.okButton],
      centered: true,
    });
  }

  deleteNewPackage() {
    this.deleteNewPackageSubject$.next(true);
  }

  cancelModification() {
    this.cancelModificationSubject$.next(true);
  }

  saveChanges() {
    this.createApplicantFormGroupService.canOut = true;
    this.createApplicantFormGroupService.submittedSubject$.next(true);
    if (this.formGroup.invalid) {
      this.openInformationMissingModal();
    } else {
      this.saveChangesSubject$.next(true);
    }
  }

  reSendEmail() {
    this.reSendEmailSubject$.next(true);
  }

  getTaxes(currentPackage: Package) {
    const { id, applicants } = currentPackage;
    const data = {
      type: TaxTypes.IMMIGRATION_BENEFIT,
      packageId: id,
      packageObj: {
        applicants
      }
    };
    return this.taxesService.postEstimatedTax(data);
  }

  hasAnyApplicantDoesNotHaveBenefitCategory() {
    return this.applicantsFormArray.getRawValue().some((applicant) => applicant.benefitCategory == '');
  }
}
