import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, map, mapTo, pluck, switchMap, withLatestFrom } from 'rxjs/operators';

import { Package } from '../../../core/models/package/package.model';
import { ModalService, NotificationsService, PackagesService } from '../../../core/services';
import { PackageApplicant } from '../../../core/models/package/package-applicant.model';
import { ConfirmButtonType } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { PackageStatus } from '../../../core/models/package/package-status.enum';
import { ActivePackageComponent } from '../../../components/active-package/active-package.component';
import { ApplicantType } from '../../../core/models/applicantType.enum';
import { RequestState } from '../../../core/ngrx/utils';

@Component({
  selector: 'app-package-applicants',
  templateUrl: './package-applicants.component.html',
  styleUrls: [ './package-applicants.component.scss' ]
})
@DestroySubscribers()
export class PackageApplicantsComponent implements OnInit, OnDestroy, AddSubscribers {
  package$: Observable<Package>;
  isPackageTransferred$: Observable<boolean>;
  isPackageLead$: Observable<boolean>;
  hideResendInvitationBttn$: Observable<boolean>;
  resendTitle$: Observable<string>;
  changePackageOwedPatchRequest$: Observable<RequestState<Package>>;

  owedFormControl = new FormControl(null, Validators.min(0));
  formGroup: FormGroup;
  applicantsFormArray: FormGroup;
  private subscribers: any = {};
  private openEditAmountOwedModal$ = new Subject();
  private resendInvitationSubject$ = new Subject();
  isResendInvitationBtnClicked = false;
  packageStatus = PackageStatus;

  constructor(
    private packagesService: PackagesService,
    private notificationsService: NotificationsService,
    private router: Router,
    private modalService: ModalService,
  ) {

  }

  get applicantFormGroup() {
    return this.applicantsFormArray.get('applicants') as FormGroup;
  }

  ngOnInit() {
    this.changePackageOwedPatchRequest$ = this.packagesService.changePackageOwedPatchRequest$;
    this.notificationsService.showComponent$.next(ActivePackageComponent);
    this.package$ = this.packagesService.package$;
    this.package$.pipe(
      filter((currentPackage) => !!currentPackage),
      map((currentPackage) => this.createApplicantApplicantsFormGroup(currentPackage))
    ).subscribe();

    this.isPackageTransferred$ = this.package$.pipe(
      filter((currentPackage) => !!currentPackage),
      map((currentPackage) => currentPackage.status == PackageStatus.TRANSFERRED)
    );

    this.isPackageLead$ = this.package$.pipe(
      filter((currentPackage) => !!currentPackage),
      map((currentPackage) => currentPackage.status == PackageStatus.LEAD)
    );

    const resendInvitationPackageStatusList: PackageStatus[] = [
      PackageStatus.LEAD,
      PackageStatus.OPEN
    ];

    this.hideResendInvitationBttn$ = this.package$.pipe(
      filter((currentPackage) => !!currentPackage),
      map((currentPackage) => !resendInvitationPackageStatusList.includes(currentPackage.status))
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
  }

  addSubscribers() {
    this.subscribers.owedControlSubscribtion = this.package$.pipe(
      filter((currentPackage) => !!currentPackage),
      pluck('owed')
    ).subscribe((owed) =>
      this.owedFormControl.patchValue(owed || 0)
    );

    this.subscribers.editOwedModalSubscription = this.openEditAmountOwedModal$.pipe(
      withLatestFrom(this.package$),
      switchMap(([ ngTemplate, packageItem ]) =>
        this.createEditAmountOwedModal(ngTemplate).pipe(
          catchError(() => {
            this.owedFormControl.reset(packageItem.owed);
            return EMPTY;
          }),
          mapTo(packageItem),
        )
      ),
      filter((packageItem) => this.owedFormControl.value !== packageItem.owed),
      filter(() => this.owedFormControl.valid),
      switchMap((packageItem) =>
        this.packagesService.updatePackageOwed({ id: packageItem.id, owed: this.owedFormControl.value })
      ),
      catchError(() => EMPTY)
    ).subscribe(() => {
      this.goToClientsPage();
    });

    this.subscribers.sendInvitationSubscription = this.resendInvitationSubject$.pipe(
      withLatestFrom(this.packagesService.package$),
      switchMap(([ , item ]) => this.packagesService.updatePackageWithoutReminder({
          id: item.id,
          applicants: this.applicantFormGroup.getRawValue(),
          representative: item.representativeId
        }).pipe(
        map(() => item),
        catchError(() => EMPTY)
        )
      ),
    ).subscribe((item) => {
      this.isResendInvitationBtnClicked = false;
      if (item.status === PackageStatus.LEAD) {
        this.router.navigate([ 'task-queue', 'package', item.id, 'welcome-email' ]);
      } else if (item.status === PackageStatus.OPEN) {
        this.router.navigate([ 'task-queue', 'clients', item.id, 'invitation-to-register' ]);
      } else {
        this.router.navigate([ 'task-queue', 'clients' ]);
      }
    });
  }

  ngOnDestroy() {
    this.notificationsService.showComponent$.next(null);
  }

  createApplicantFormGroup(data) {
    return new FormGroup({
      id: new FormControl({ value: data.id || null, disabled: true }),
      firstName: new FormControl({ value: data.firstName || null, disabled: true }),
      middleName: new FormControl({ value: data.middleName || null, disabled: true }),
      lastName: new FormControl({ value: data.lastName || null, disabled: true }),
      easyVisaId: new FormControl({ value: data.easyVisaId || null, disabled: true }),
      email: new FormControl({ value: data.email || null, disabled: true }),
      dateOfBirth: new FormControl({ value: data.dateOfBirth || null, disabled: true }),
      homeAddress: new FormGroup({
        city: new FormControl(data && data.homeAddress ? data.homeAddress.city : null),
        country: new FormControl(data && data.homeAddress ? data.homeAddress.country : null),
        line1: new FormControl(data && data.homeAddress ? data.homeAddress.line1 : null),
        line2: new FormControl(data && data.homeAddress ? data.homeAddress.line2 : null),
        postalCode: new FormControl(data && data.homeAddress ? data.homeAddress.postalCode : null),
        province: new FormControl(data && data.homeAddress ? data.homeAddress.province : null),
        state: new FormControl(data && data.homeAddress ? data.homeAddress.state : null),
        zipCode: new FormControl(data && data.homeAddress ? data.homeAddress.zipCode : null),
      }),
      mobileNumber: new FormControl({ value: data.mobileNumber || null, disabled: true }),
      homeNumber: new FormControl({ value: data.homeNumber || null, disabled: true }),
      workNumber: new FormControl({ value: data.workNumber || null, disabled: true }),
      timeZone: new FormControl({ value: data.timeZone || null, disabled: true }),
    });
  }

  createBeneficiaryFormGroup(data: PackageApplicant) {
    return this.formGroup = new FormGroup({
        benefitCategory: new FormControl(data.benefitCategory || null),
        fee: new FormControl(data.fee || 0),
        applicantType: new FormControl(data.applicantType || null),
        citizenshipStatus: new FormControl(data.citizenshipStatus || null),
        profile: this.createApplicantFormGroup(data.profile || null),
        inviteApplicant: new FormControl(data.inviteApplicant || false)
      },
    );
  }

  createApplicantApplicantsFormGroup(data) {
    return this.applicantsFormArray = new FormGroup({
      applicants: this.createApplicantApplicantsFormArray(data),
    });
  }

  createApplicantApplicantsFormArray(data) {
    const applicantsFormArray = new FormArray([]);
    data.applicants.map((applicant) => {
      const applicantFormGroup = this.createBeneficiaryFormGroup(applicant);
      return applicantsFormArray.push(applicantFormGroup);
    });
    return applicantsFormArray;
  }


  openEditAmountOwedModal(ngTemplate) {
    this.openEditAmountOwedModal$.next(ngTemplate);
  }

  applicantBgColor(applicantType, index) {
    if (index === 0) {
      return 'bg-f0f6fa';
    } else {
      switch (applicantType) {
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

  resendInvitation() {
    this.isResendInvitationBtnClicked = true;
    this.resendInvitationSubject$.next();
  }

  goToClientsPage() {
    this.router.navigate([ 'task-queue', 'clients' ]);
  }

  private createEditAmountOwedModal(ngTemplate) {
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
      header: 'TEMPLATE.TASK_QUEUE.CLIENTS.EDIT_AMOUNT_OWED.TITLE',
      body: ngTemplate,
      buttons,
    });
  }

  getFullNameWithEVId(profile: any): string {
    return profile ? `${profile.firstName} ${profile.lastName} (ID: ${profile.easyVisaId})` : ``;
  }
}
