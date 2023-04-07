import { Component, EventEmitter, Input, OnDestroy, OnInit, Output, ViewChild } from '@angular/core';
import { FormControl } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { EMPTY, NEVER, Observable, Subject } from 'rxjs';
import {
  catchError,
  filter,
  map,
  mapTo,
  pluck,
  skip,
  switchMap,
  take,
  withLatestFrom
} from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ConfirmButtonType } from '../../../../../../core/modals/confirm-modal/confirm-modal.component';
import { PackageApplicant } from '../../../../../../core/models/package/package-applicant.model';
import { RequestState, ResponseStatus } from '../../../../../../core/ngrx/utils';
import { ModalService, OrganizationService, PackagesService } from '../../../../../../core/services';
import { PackageStatus } from '../../../../../../core/models/package/package-status.enum';
import { CreateApplicantFormGroupService } from '../../../../services';


@Component({
  selector: 'app-email-address',
  templateUrl: './email-address.component.html',
})
@DestroySubscribers()
export class EmailAddressComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() emailFormControl: FormControl;
  @Input() EVIdFormControl: FormControl;
  @Input() inviteApplicantFormControl: FormControl;
  @Input() applicantTypeFormControl: FormControl;
  @Input() isEmailVerifiedFormControl: FormControl;
  @Input() index: number;

  @Output() applicantData = new EventEmitter();

  @ViewChild('differentEmailRequiredModal', {static: true}) differentEmailRequiredModal;
  @ViewChild('goodNewsModal', {static: true}) goodNewsModal;
  @ViewChild('verifyWithClientModal', {static: true}) verifyWithClientModal;
  @ViewChild('emailFormatInvalidModal', {static: true}) emailFormatInvalidModal;
  @ViewChild('applicantMemberOfBlockedPackageModal', {static: true}) applicantMemberOfBlockedPackageModal;
  @ViewChild('beneficiaryMemberOfOpenPackageModal', {static: true}) beneficiaryMemberOfOpenPackageModal;


  applicantProfile$: Observable<PackageApplicant>;
  getApplicantRequest$: Observable<RequestState<PackageApplicant | HttpErrorResponse>>;
  isApplicantRequestLoading$: Observable<boolean>;
  packageStatus$: Observable<string>;
  activeOrganizationId$: Observable<string>;
  isFormSubmitted$: Observable<boolean>;

  private verifyEmailSubject$: Subject<string> = new Subject<string>();

  private subscribers: any = {};

  PackageStatus = PackageStatus;

  constructor(
    private packagesService: PackagesService,
    private modalService: ModalService,
    private organizationService: OrganizationService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
  ) {

  }

  ngOnInit() {
    this.isFormSubmitted$ = this.createApplicantFormGroupService.submittedSubject$;
    this.activeOrganizationId$ = this.organizationService.activeOrganizationId$;
    this.packageStatus$ = this.packagesService.package$.pipe(
      filter((item) => !!item),
      map((item) => item.status)
    );
    this.getApplicantRequest$ = this.packagesService.getApplicantRequest$;
    this.isApplicantRequestLoading$ = this.packagesService.getApplicantRequest$.pipe(
      map(response => response?.loading)
    );
    this.applicantProfile$ = this.getApplicantRequest$.pipe(
      filter(response => response.status === ResponseStatus.success),
      map((res) => res?.data as PackageApplicant)
    );
  }

  addSubscribers() {
    this.subscribers.getApplicantFailedSubscription = this.verifyEmailSubject$.pipe(
      switchMap(() => this.getApplicantRequest$.pipe(
        skip(1),
        filter(response => response.status !== ''),
        take(1),
        filter(response => response.status === ResponseStatus.fail),
      )),
      pluck('data'),
    ).subscribe((error: HttpErrorResponse) => {
      if (error.status === 404) {
        this.emailFormControl.setErrors(null, {emitEvent: false});
        this.applicantData.emit(null);
        this.isEmailVerifiedFormControl.patchValue(true);
        if (this.inviteApplicantFormControl) {
          this.inviteApplicantFormControl.enable({emitEvent: false});
        }
        return this.openEmailVerificationModal(
          this.verifyWithClientModal,
          'TEMPLATE.TASK_QUEUE.VERIFY_WITH_CLIENT_MODAL.TITLE').pipe(catchError(() => NEVER));
      } else if (error.status === 423) {
        return this.openEmailVerificationModal(
          this.differentEmailRequiredModal,
          'TEMPLATE.TASK_QUEUE.EMAIL_IS_ASSOCIATED_WITH_TITLE'
        ).pipe(catchError(() => NEVER));
      } else if (error.status === 422) {
        return this.modalService.showErrorModal(error.error.errors || [error.error]);
      }
    });

    this.subscribers.verifyEmailSubscription = this.verifyEmailSubject$.pipe(
      filter(() => this.emailFormControl.value),
      withLatestFrom(this.activeOrganizationId$),
      switchMap(([email, activeOrganizationId]) =>
        this.packagesService.getApplicant({
          email,
          organizationId: activeOrganizationId
        }).pipe(
          take(1),
          catchError(() => EMPTY),
        )
      ),
      switchMap((applicant) =>
        this.openEmailVerificationModal(this.goodNewsModal).pipe(
          catchError(() => {
            this.emailFormControl.reset('', {emitEvent: false});
            return NEVER;
          }),
          mapTo(applicant)
        ),
      ),
    )
    .subscribe((applicant: PackageApplicant & { inBlockedPackage: boolean; inOpenPackage: boolean }) => {
      this.isEmailVerifiedFormControl.patchValue(true);
      this.inviteApplicantFormControl.enable({emitEvent: false});
      this.applicantData.emit({...applicant.profile, isEmailVerified: true});
      this.openApplicantsInAnotherPackagesModals(applicant);
    });

    this.subscribers.emailValueChanges = this.emailFormControl.valueChanges
    .subscribe((value) => {
      this.inviteApplicantFormControl.patchValue(false, {emitEvent: false});
      this.inviteApplicantFormControl.disable({emitEvent: false});

      this.isEmailVerifiedFormControl.patchValue(value ? null : true);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  verifyEmail() {
    this.verifyEmailSubject$.next(this.emailFormControl.value);
  }

  openApplicantsInAnotherPackagesModals(response) {
    if (response && response.inOpenPackage) {
      this.openEmailVerificationModal(
        this.beneficiaryMemberOfOpenPackageModal,
        'TEMPLATE.TASK_QUEUE.BENEFICIARY_MEMBER_OF_OPEN_PACKAGE_MODAL.HEADER'
      );
    }
    if (response && response.inBlockedPackage) {
      this.openEmailVerificationModal(
        this.applicantMemberOfBlockedPackageModal,
        'TEMPLATE.TASK_QUEUE.APPLICANT_MEMBER_OF_BLOCKED_PACKAGE_MODAL.HEADER'
      );
    }
  }

  openEmailVerificationModal(content, title = 'TEMPLATE.TASK_QUEUE.GOOD_NEWS_MODAL.TITLE') {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    const goodNewsButtons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'TEMPLATE.TASK_QUEUE.APPLICANT.INVITE_TO_JOIN_PACKAGE',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: title,
      body: content,
      buttons: content === this.goodNewsModal ? goodNewsButtons : buttons,
      showCloseIcon: true,
      size: 'lg',
      centered: true,
    });
  }
}
