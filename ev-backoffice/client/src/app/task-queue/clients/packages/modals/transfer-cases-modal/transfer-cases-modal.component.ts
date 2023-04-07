import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Dictionary } from '@ngrx/entity';

import { EMPTY, Observable, ReplaySubject, Subject } from 'rxjs';
import { catchError, debounceTime, filter, map, switchMap, tap, withLatestFrom } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ModalService, OrganizationService, PackagesService } from '../../../../../core/services';
import { FindLabelPipe } from '../../../../../shared/pipes/find-label/find-label.pipe';
import { getPetitioner } from '../../../../../shared/utils/get-petitioner';
import { createPackageStatusLabel } from '../../../../../shared/utils/create-package-status-label';
import { OrganizationType } from '../../../../../core/models/organization-type.enum';
import { Organization } from '../../../../../core/models/organization.model';
import { AttorneyMenu } from '../../../../../core/models/attorney.model';
import { Package } from '../../../../../core/models/package/package.model';
import { PackageStatus } from '../../../../../core/models/package/package-status.enum';
import { states } from '../../../../../core/models/states';
import { EMAIL_PATTERN } from '../../../../../shared/validators/constants/email-pattern.const';

import { TableDataFormat } from '../../../../models/table-data-format.model';
import { citizenshipStatusValue } from '../../../../models/citizenship-status.model';
import {RequestState} from "../../../../../core/ngrx/utils";
import {RecipientModel} from "../../../../../core/models/recipient.model";

export interface TableData {
  id: number;
  status: TableDataFormat;
  applicants: TableDataFormat;
  representativeStatus: TableDataFormat;
  benefits?: TableDataFormat;
  ques?: TableDataFormat;
  docs?: TableDataFormat;
  lastActive?: TableDataFormat;
  owed?: TableDataFormat;
}

@Component({
  selector: 'app-transfer-cases-modal',
  templateUrl: './transfer-cases-modal.component.html',
})

@DestroySubscribers()
export class TransferCasesModalComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() selectedPackagesIds: number[];

  verifyAttorneyRequestState$: Observable<any>;
  isSoloPractitioner$: Observable<boolean>;
  activeOrganization$: Observable<Organization>;
  currentRepresentative$: Observable<AttorneyMenu>;
  currentRepresentativeId$: Observable<number>;
  transfereeOrganizations$: Observable<Organization[]>;
  representativesMenu$: Observable<AttorneyMenu[]>;
  packageEntities$: Observable<Dictionary<Package>>;
  tableData$: Observable<TableData[]>;
  messageSubject$: ReplaySubject<any> = new ReplaySubject<any>(1);
  packagesTransferPostRequest$: Observable<RequestState<RecipientModel>> = this.packagesService.packagesTransferPostRequest$;

  tableHeader: { title: string; colName: string }[];
  verifyRecipientFormGroup: FormGroup;
  transferCasesFormGroup: FormGroup;
  isSameOrganizationMemberFormControl: FormControl;

  private verifyRecipientSubject$: Subject<boolean> = new Subject();
  private transferCasesSubject$: Subject<boolean> = new Subject();
  private subscribers: any = {};

  get emailFormControl() {
    return this.verifyRecipientFormGroup.get('email');
  }

  get easyVisaIdFormControl() {
    return this.verifyRecipientFormGroup.get('easyVisaId');
  }

  get representativeIdFormControl() {
    return this.transferCasesFormGroup.get('representativeId');
  }

  get organizationIdFormControl() {
    return this.transferCasesFormGroup.get('organizationId');
  }

  get isShowWarning$() {
    return this.verifyAttorneyRequestState$.pipe(
        filter((res) => res.status === 'fail' && !res.loaded),
        map(() =>
          !this.easyVisaIdFormControl.hasError('required')
          && !this.easyVisaIdFormControl.hasError('pattern')
          && !this.emailFormControl.hasError('required')
          && !this.emailFormControl.hasError('pattern')
          && !this.representativeIdFormControl.value
        )
      );
  }

  constructor(
    private activeModal: NgbActiveModal,
    private organizationService: OrganizationService,
    private packagesService: PackagesService,
    private modalService: ModalService,
    private findLabel: FindLabelPipe,
    private datePipe: DatePipe
  ) {
  }

  ngOnInit() {
    this.verifyAttorneyRequestState$ = this.organizationService.verifyAttorneyRequestState$;
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.currentRepresentativeId$ = this.organizationService.currentRepresentativeId$;
    this.currentRepresentative$ = this.organizationService.currentRepresentative$;
    this.representativesMenu$ = this.organizationService.representativesMenu$;
    this.packageEntities$ = this.packagesService.packageEntities$;
    this.isSameOrganizationMemberFormControl = new FormControl(null);
    this.tableHeader = [
      {title: 'TEMPLATE.TASK_QUEUE.CLIENTS.STATUS', colName: 'status'},
      {title: 'TEMPLATE.TASK_QUEUE.NAV.CLIENTS', colName: 'applicants'},
      {title: 'TEMPLATE.TASK_QUEUE.CLIENTS.LEGAL_STATUS', colName: 'representativeStatus'},
      {title: 'TEMPLATE.TASK_QUEUE.CLIENTS.BENEFIT', colName: 'benefits'},
      {title: 'TEMPLATE.TASK_QUEUE.CLIENTS.QUES', colName: 'ques'},
      {title: 'TEMPLATE.TASK_QUEUE.CLIENTS.DOCS', colName: 'docs'},
      {title: 'TEMPLATE.TASK_QUEUE.CLIENTS.LAST_ACTIVE', colName: 'lastActive'},
      {title: 'TEMPLATE.TASK_QUEUE.CLIENTS.OWED', colName: 'owed'},
    ];

    this.tableData$ = this.packageEntities$.pipe(
      map((packageEntities) => this.selectedPackagesIds.map((packageId) => ({
            id: packageId,
            status: {
              data: createPackageStatusLabel(packageEntities[packageId].status),
              class: packageEntities[packageId].status === PackageStatus.BLOCKED ? 'text-danger' : 'text-blue'
            },
            applicants: {
              data: packageEntities[packageId].title,
            },
            representativeStatus: {
              data: this.findLabel.transform(getPetitioner(packageEntities[packageId]).citizenshipStatus, citizenshipStatusValue),
            },
            benefits: {
              data: [
                ...packageEntities[packageId].applicants.map((applicant) => applicant.benefitCategory)
              ].join(' '),
            },
            lastActive: {
              data: packageEntities[packageId].lastActiveOn ? this.datePipe.transform(new Date(packageEntities[packageId].lastActiveOn), 'MM/dd/yyyy') : ''
            },
            docs: {
              data: `${packageEntities[packageId].documentCompletedPercentage}%` || ''
            },
            ques: {
              data: `${packageEntities[packageId].questionnaireCompletedPercentage}%`|| ''
            },
            owed: {
              data: packageEntities[packageId].owed
            }
          })))
    );

    this.transfereeOrganizations$ = this.verifyAttorneyRequestState$.pipe(
      filter((res) => res.status === 'success' && res.loaded && !res.loading),
      map((res) => {
        if (res.data.organizations.length === 1) {
          const organizationId = res.data.organizations.find((organization) => !!organization.id).id;
          this.organizationIdFormControl.patchValue(organizationId);
        }
        return res.data.organizations;
      })
    );

    this.isSoloPractitioner$ = this.activeOrganization$.pipe(
      map((organization) => organization.organizationType === OrganizationType.SOLO_PRACTICE)
    );

    this.verifyRecipientFormGroup = new FormGroup({
      easyVisaId: new FormControl(null, [
        Validators.required,
        Validators.pattern('(^[A-Z]\\d{10}$)'),
      ]),
      email: new FormControl(null, [Validators.required, Validators.pattern(EMAIL_PATTERN)]),
    });

    this.transferCasesFormGroup = new FormGroup({
      packageIds: new FormControl(this.selectedPackagesIds, Validators.required),
      representativeId: new FormControl(null, Validators.required),
      organizationId: new FormControl(null, Validators.required),
    });
  }

  addSubscribers() {
    this.subscribers.verifySubscription = this.verifyRecipientFormGroup.valueChanges.pipe(
      debounceTime(2000),
      tap(() => this.messageSubject$.next([])),
      filter(() => this.verifyRecipientFormGroup.valid),
      switchMap(() => this.organizationService.verifyAttorney(this.verifyRecipientFormGroup.value).pipe(
        catchError((error) => {
          if (error.status !== 401) {
            this.messageSubject$.next(error.error.errors);
          }
          this.transferCasesFormGroup.patchValue({representativeId: null});
          this.transferCasesFormGroup.patchValue({organizationId: null});
          return EMPTY;
        })
      )),
    ).subscribe((res: { representativeId: number }) => {
      this.transferCasesFormGroup.patchValue({representativeId: res.representativeId});
    });

    this.subscribers.valueChangedSubscription = this.verifyRecipientFormGroup.valueChanges.pipe(
    ).subscribe(() => this.transferCasesFormGroup.patchValue({representativeId: null}));

    this.subscribers.isSameOrganizationMemberSubscription = this.isSameOrganizationMemberFormControl.valueChanges.pipe(
      withLatestFrom(this.organizationService.activeOrganizationId$),
    ).subscribe(([value, activeOrganizationId]) => {
      if (value) {
        this.organizationIdFormControl.patchValue(activeOrganizationId, {emitEvent: false});
        this.easyVisaIdFormControl.patchValue(null, {emitEvent: false});
        this.emailFormControl.patchValue(null, {emitEvent: false});
      } else {
        this.organizationIdFormControl.reset(null, {emitEvent: false});
      }
      this.transferCasesFormGroup.patchValue({representativeId: null});
    });

    this.subscribers.transferCasesSubscription = this.transferCasesSubject$.pipe(
      filter(() => this.transferCasesFormGroup.valid),
      switchMap(() => this.packagesService.packagesTransfer({
        ...this.transferCasesFormGroup.value,
      }).pipe(catchError((error: HttpErrorResponse)=>EMPTY),))
    ).subscribe(() => this.modalDismiss());
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  modalDismiss() {
    this.activeModal.close();
  }

  packagesTransfer() {
    this.transferCasesSubject$.next(true);
  }

  verifyRecipient() {
    this.verifyRecipientSubject$.next(true);
  }
}
