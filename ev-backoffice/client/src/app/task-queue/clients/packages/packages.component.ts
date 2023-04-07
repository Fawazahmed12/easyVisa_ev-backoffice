import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { DatePipe } from '@angular/common';

import { every, head, isEqual, orderBy } from 'lodash-es';
import { combineLatest, EMPTY, Observable, of, Subject } from 'rxjs';
import { fromPromise } from 'rxjs/internal-compatibility';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  pairwise,
  shareReplay,
  skip,
  startWith,
  switchMap,
  take,
  withLatestFrom,
  tap
} from 'rxjs/operators';

import { Dictionary } from '@ngrx/entity/src/models';
import { TranslatePipe } from '@ngx-translate/core';

import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { getRepresentativeLabel } from '../../../shared/utils/get-representative-label';
import { TableHeader } from '../../../components/table/models/table-header.model';
import { Organization } from '../../../core/models/organization.model';
import {
  ConfigDataService,
  ModalService,
  NotificationsService,
  OrganizationService,
  PackagesService,
  UserService,
} from '../../../core/services';
import { User } from '../../../core/models/user.model';
import { PackageStatus } from '../../../core/models/package/package-status.enum';
import { Package } from '../../../core/models/package/package.model';
import { createPackageStatusLabel } from '../../../shared/utils/create-package-status-label';
import { OrganizationType } from '../../../core/models/organization-type.enum';
import { RequestState } from '../../../core/ngrx/utils';
import { Attorney, AttorneyMenu } from '../../../core/models/attorney.model';
import { EmployeePosition } from '../../../account/permissions/models/employee-position.enum';
import { getPetitioner } from '../../../shared/utils/get-petitioner';
import { FindLabelPipe } from '../../../shared/pipes/find-label/find-label.pipe';
import { ActivePackageComponent } from '../../../components/active-package/active-package.component';
import { BenefitCategoryModel } from '../../../core/models/benefits.model';
import { CompareParams } from '../../../core/models/compare-params.model';
import { TableDataFormat } from '../../models/table-data-format.model';
import { citizenshipStatusValue } from '../../models/citizenship-status.model';

import { ChangeCaseStatusModalComponent } from './modals/change-case-status-modal/change-case-status-modal.component';
import { ClientSearchModalComponent } from './modals/client-search-modal/client-search-modal.component';
import { SearchParams } from './models/search-params.model';
import { CannotTransferModalComponent } from './modals/cannot-transfer-modal/cannot-transfer-modal.component';
import { DeleteOldLeadsModalComponent } from './modals/delete-old-leads-modal/delete-old-leads-modal.component';
import { TransferCasesModalComponent } from './modals/transfer-cases-modal/transfer-cases-modal.component';
import { CannotDeletePackagesModalComponent } from './modals/cannot-delete-packages-modal/cannot-delete-packages-modal.component';
import { ConfirmDeletePackagesModalComponent } from './modals/confirm-delete-packages-modal/confirm-delete-packages-modal.component';
import { benefitCategories } from '../../../core/models/benefit-categories';
import { BenefitCategories } from '../../../core/models/benefit-categories.enum';
import {
  AccessDeniedForTraineeModalComponent
} from './modals/access-denied-for-trainee-modal/access-denied-for-trainee-modal.component';
import { PaginationService } from "../../../core/services/pagination.service";


export interface TableData {
  id: number;
  checkbox: FormControl;
  status: TableDataFormat;
  applicants: TableDataFormat;
  representative: TableDataFormat;
  representativeStatus: TableDataFormat;
  benefits?: TableDataFormat;
  ques?: TableDataFormat;
  docs?: TableDataFormat;
  lastActive?: TableDataFormat;
  owed?: TableDataFormat;
  active: boolean;
}

@Component({
  selector: 'app-packages',
  templateUrl: './packages.component.html',
  styleUrls: ['./packages.component.scss'],
  providers: [TranslatePipe]
})
@DestroySubscribers()
export class PackagesComponent implements OnInit, OnDestroy, AddSubscribers {
  activeOrganization$: Observable<Organization> = this.organizationService.activeOrganization$;
  activeOrganizationId$: Observable<string> = this.organizationService.activeOrganizationId$;
  representativesRequestState$: Observable<RequestState<Attorney[]>>;
  allBenefitCategories$: Observable<BenefitCategoryModel[]>;

  formGroup: FormGroup;
  selectedPackagesIds: FormControl = new FormControl([]);

  tableHeader$: Observable<TableHeader[]> = new Observable<TableHeader[]>();
  tableData$: Observable<TableData[]> = new Observable<TableData[]>();
  total$: Observable<number> = this.packagesService.total$;
  packages$: Observable<Package[]>;
  packageEntities$: Observable<Dictionary<Package>>;
  changeCaseStatusSubject$: Subject<string> = new Subject<string>();
  currentUser$: Observable<User>;
  activePackageId$: Observable<string | number>;
  isTransferCasesEnable$: Observable<boolean>;
  isTrainee$: Observable<boolean>;
  formGroupValueChanges$: Observable<{ isShowModal: boolean; formData: SearchParams }>;
  pairFormData$: Observable<SearchParams[]>;
  getPackagesRequest$: Observable<RequestState<Package[]>>;


  EmployeePosition = EmployeePosition;
  defaultStatusFilterValue: PackageStatus[] = [PackageStatus.LEAD, PackageStatus.OPEN, PackageStatus.BLOCKED];
  allStatusFilterValue: PackageStatus[] = [PackageStatus.LEAD, PackageStatus.OPEN, PackageStatus.BLOCKED, PackageStatus.CLOSED, PackageStatus.TRANSFERRED];
  showAllStatusesFormControl: FormControl = new FormControl(true);
  showDefaultViewFormControl: FormControl = new FormControl(true);
  private searchForClientsSubject$: Subject<boolean> = new Subject<boolean>();
  private setActivePackageSubject$: Subject<string> = new Subject<string>();
  private openTransferCasesModalSubject$: Subject<boolean> = new Subject<boolean>();
  private deleteLeadsByDateRange$: Subject<void> = new Subject();
  private deleteSelectedLeads$: Subject<boolean> = new Subject<boolean>();
  private deleteSelectedTransfers$: Subject<boolean> = new Subject<boolean>();
  private deSelectPackages$: Subject<PackageStatus> = new Subject<PackageStatus>();
  private subscribers: any = {};

  @ViewChild('noResults') noResultsTemplate;

  get maxFormControl() {
    return this.formGroup.get('max');
  }

  get offsetFormControl() {
    return this.formGroup.get('offset');
  }

  get representativeIdFormControl() {
    return this.formGroup.get('representativeId');
  }

  get sortFormControl() {
    return this.formGroup.get('sort');
  }

  get orderFormControl() {
    return this.formGroup.get('order');
  }

  get page() {
    return (this.offsetFormControl.value / this.maxFormControl.value) + 1;
  }

  get selectedPackagesIdsControlValue$() {
    return this.selectedPackagesIds.valueChanges.pipe(
      startWith(this.selectedPackagesIds.value),
      shareReplay(1)
    );
  }

  get showAllStatusesFormControlValue$() {
    return this.showAllStatusesFormControl.valueChanges.pipe(
      startWith(this.showAllStatusesFormControl.value),
      shareReplay(1)
    );
  }

  get representativeIdControlValue$() {
    return this.representativeIdFormControl.valueChanges.pipe(
      startWith(this.representativeIdFormControl.value),
      shareReplay(1)
    );
  }

  get formGroupValue$() {
    return this.formGroup.valueChanges.pipe(
      startWith(this.formGroup.value),
      shareReplay(1)
    );
  }

  constructor(
    private activatedRoute: ActivatedRoute,
    private packagesService: PackagesService,
    private router: Router,
    private userService: UserService,
    private ngbModal: NgbModal,
    private organizationService: OrganizationService,
    private modalService: ModalService,
    private findLabel: FindLabelPipe,
    private translatePipe: TranslatePipe,
    private notificationsService: NotificationsService,
    private configDataService: ConfigDataService,
    private datePipe: DatePipe,
    private paginationService: PaginationService
  ) {
    this.createForm();
  }

  ngOnInit() {
    this.notificationsService.showComponent$.next(ActivePackageComponent);
    this.getPackagesRequest$ = this.packagesService.getPackagesRequest$;
    this.representativesRequestState$ = this.organizationService.representativesRequestState$;
    this.packages$ = this.packagesService.packages$;
    this.packageEntities$ = this.packagesService.packageEntities$;
    this.currentUser$ = this.userService.currentUser$;
    this.isTrainee$ = this.organizationService.currentPosition$.pipe(
      map((currentPosition) => currentPosition === EmployeePosition.TRAINEE)
    );
    this.allBenefitCategories$ = this.configDataService.allBenefitCategories$;

    this.tableHeader$ = this.activeOrganization$.pipe(
      filter(organization => !!organization),
      map((organization) => {
        const representativeType = getRepresentativeLabel(organization.organizationType);
        return [
          {
            title: '',
            colName: 'checkbox',
            colClass: 'text-center checkbox-col'
          },
          {
            title: 'TEMPLATE.TASK_QUEUE.CLIENTS.STATUS',
            colName: 'status',
            sortBy: true,
            action: true,
            colClass: 'cursor-pointer text-center width-7 px-0'
          },
          {
            title: 'TEMPLATE.TASK_QUEUE.NAV.CLIENTS',
            colName: 'applicants',
            sortBy: true,
            colClass: 'width-25 text-truncate',
          },
          {
            title: representativeType,
            colName: 'representative',
            sortBy: true,
            colClass: 'width-15 text-truncate px-3'
          },
          {
            title: 'TEMPLATE.TASK_QUEUE.CLIENTS.LEGAL_STATUS',
            colName: 'representativeStatus',
            colClass: 'text-center width-12 px-0'
          },
          {
            title: 'TEMPLATE.TASK_QUEUE.CLIENTS.BENEFIT',
            colName: 'benefits',
            colClass: 'width-10 px-2 text-truncate'
          },
          {
            title: 'TEMPLATE.TASK_QUEUE.CLIENTS.QUES',
            colName: 'ques',
            sortBy: true,
            colClass: 'width-7 px-2 text-right'
          },
          {
            title: 'TEMPLATE.TASK_QUEUE.CLIENTS.DOCS',
            colName: 'docs',
            sortBy: true,
            colClass: 'width-7 px-2 text-right'
          },
          {
            title: 'TEMPLATE.TASK_QUEUE.CLIENTS.LAST_ACTIVE',
            colName: 'lastActive',
            sortBy: true,
            colClass: 'width-10 px-2 text-right'
          },
          {
            title: 'TEMPLATE.TASK_QUEUE.CLIENTS.OWED',
            colName: 'owed',
            sortBy: true,
            colClass: 'width-7 px-2 text-right'
          },
        ];
      })
    );

    this.activePackageId$ = this.packagesService.activePackageId$;

    this.tableData$ = combineLatest([
      this.packages$,
      this.activePackageId$,
    ]).pipe(
      withLatestFrom(
        this.selectedPackagesIdsControlValue$,
        this.allBenefitCategories$,
      ),
      map(([[packages, id], selectedPackagesIds, allBenefitCategories]) => packages.map((currentPackage) => {
        const isChecked = !!selectedPackagesIds.find(checkedId => checkedId === currentPackage.id);
        const benefitLabels = currentPackage.applicants
          .map((applicant) => {
            const isSearchLabelRequired = [BenefitCategories.LPRCHILD.valueOf(), BenefitCategories.LPRSPOUSE.valueOf()].includes(applicant.benefitCategory);
            return this.translatePipe.transform(this.findLabel.transform(applicant.benefitCategory, allBenefitCategories, null, isSearchLabelRequired));
          })
          .filter(benefitLabel => !!benefitLabel)
          .join(', ');

        return {
          id: currentPackage.id,
          checkbox: new FormControl(isChecked),
          status: {
            data: createPackageStatusLabel(currentPackage.status),
            class: currentPackage.status === PackageStatus.BLOCKED ? 'text-danger' : 'text-blue',
          },
          applicants: {
            data: currentPackage.title,
          },
          representative: {
            data: this.getRepresentativeNameData(currentPackage),
            class: this.getRepresentativeNameData(currentPackage, true),
          },
          representativeStatus: {
            data: this.findLabel.transform(
              getPetitioner(currentPackage) &&
              getPetitioner(currentPackage).citizenshipStatus, citizenshipStatusValue
            ),
          },
          benefits: {
            data: benefitLabels,
          },
          active: currentPackage.id === id,
          owed: { data: currentPackage.owed },
          lastActive: {
            data: currentPackage.lastActiveOn ? this.datePipe.transform(new Date(currentPackage.lastActiveOn), 'MM/dd/yyyy') : ''
          },
          docs: {
            data: `${currentPackage.documentCompletedPercentage}%` || ''
          },
          ques: {
            data: `${currentPackage.questionnaireCompletedPercentage}%` || ''
          },
        };
      }))
    );

    this.isTransferCasesEnable$ = this.activeOrganization$.pipe(
      filter((activeOrganization) => !!activeOrganization),
      map((activeOrganization: Organization) =>
        activeOrganization.organizationType === OrganizationType.SOLO_PRACTICE || activeOrganization.isAdmin
      )
    );

    this.pairFormData$ = this.formGroup.valueChanges.pipe(
      distinctUntilChanged(isEqual),
      startWith(this.formGroup.getRawValue()),
      pairwise()
    );

    this.formGroupValueChanges$ = this.pairFormData$.pipe(
      withLatestFrom(
        this.organizationService.representativesMenu$,
        this.activeOrganization$,
      ),
      map(([
             pairFormData,
             ,
           ]: [
        SearchParams[],
        AttorneyMenu[],
        Organization
      ]) => {
        const [firstFormData, currentFormData] = pairFormData;
        const firstFormDataObject: any = firstFormData;
        const currentFormDataObject: any = currentFormData;
        const firstDataToCheck = new CompareParams(firstFormData);
        const currentDataToCheck = new CompareParams(currentFormData);
        const isShowModal = !isEqual(firstDataToCheck, currentDataToCheck) && firstFormDataObject.offset != currentFormDataObject.offset;

        return { isShowModal, formData: this.formGroup.getRawValue() };
      })
    );
  }

  addSubscribers() {
    this.subscribers.historyBackForward$ = this.paginationService.getHistoryNavigationSubscription(this.offsetFormControl)

    this.subscribers.queryParamsSubscription = this.activatedRoute.queryParams.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
      take(1),
    ).subscribe(([params, [repId, orgId]]) => this.formGroup.patchValue({
      ...params,
      organizationId: parseInt(params?.organizationId, 10) || orgId,
      representativeId: parseInt(params?.representativeId, 10) || repId,
    }, { emitEvent: false }));

    this.subscribers.currentRepIdMarketingSubscription = this.organizationService.currentRepIdOrgId$.pipe(
      skip(1),
    ).subscribe(([representativeId, organizationId]) => this.formGroup.patchValue({
      representativeId,
      organizationId
    }));

    this.subscribers.queryParamsFormGroupSubscription = this.formGroup.valueChanges
      .subscribe(() => this.addQueryParamsToUrl(this.formGroup.getRawValue()));

    this.subscribers.formValueFormGroupSubscription = this.formGroupValue$.pipe(
      filter(data => !!data)
    ).subscribe((params) => {
      const isDefaultStatuses = params.status?.includes(...this.defaultStatusFilterValue) && params.status?.length === 3;
      this.showAllStatusesFormControl.patchValue(isDefaultStatuses);
    });

    this.subscribers.qweFormGroupSubscription = this.formGroupValue$.pipe(
      map(data => new CompareParams(data)),
      map(params => params.isDefaultParams()),
    ).subscribe(result => this.showDefaultViewFormControl.patchValue(result));

    this.subscribers.qqqGroupSubscription = this.formGroupValueChanges$
      .subscribe(({ isShowModal, formData }) => this.packagesService.getPackages(formData, isShowModal));

    this.subscribers.getPackagesFormGroupSubscription = this.representativeIdControlValue$
      .subscribe(() => this.selectedPackagesIds.patchValue([]));

    this.subscribers.caseStatusSubscription = this.changeCaseStatusSubject$.pipe(
      switchMap((id) => this.packageEntities$.pipe(
        map((entities) => entities[ id ]),
        take(1),
      )),
      withLatestFrom(this.isTrainee$),
      filter(([item, isTrainee]) => item.status != PackageStatus.TRANSFERRED)
    ).subscribe(([item, isTrainee]) => {
      if (!isTrainee) {
        this.packagesService.selectPackage(item.id);
        const modalRef = this.ngbModal.open(ChangeCaseStatusModalComponent, {
          centered: true,
          size: 'lg'
        });
        modalRef.componentInstance.item = item;
        return fromPromise(modalRef.result);
      } else {
        const modalRef = this.ngbModal.open(AccessDeniedForTraineeModalComponent, {
          centered: true,
          size: 'md'
        });
        modalRef.componentInstance.item = item;
        return fromPromise(modalRef.result);
      }
    });

    this.subscribers.setActivePackageSubscription = this.setActivePackageSubject$.pipe(
      withLatestFrom(this.isTrainee$),
    )
      .subscribe(([activePackageId, isTrainee]) => {
          if (activePackageId) {
            this.packagesService.setActivePackage(+activePackageId);
            if (!isTrainee) {
              this.router.navigate([activePackageId, 'uscis-package-applicants'], { relativeTo: this.activatedRoute });
            }
          } else {
            this.packagesService.clearActivePackage();
          }
        }
      );

    this.subscribers.searchSubscription = this.searchForClientsSubject$.pipe(
      switchMap(() => this.openSearchModal().pipe(
        catchError(() => EMPTY)
      )),
    ).subscribe((res) => {
      this.formGroup.patchValue(new SearchParams(res));
    });

    this.subscribers.deleteOldLeadsSubscription = this.deleteLeadsByDateRange$.pipe(
      switchMap(() => this.openDeleteOldLeadsModal()),
      withLatestFrom(this.organizationService.activeOrganizationId$),
      switchMap(([params, id]) => this.packagesService.removeLeadPackages({
        ...params,
        organizationId: id
      }).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [error.error]);
          }
          return EMPTY;
        }),
      )),
      withLatestFrom(this.packagesService.activePackage$),
    ).subscribe(() => this.offsetFormControl.patchValue(0));

    this.subscribers.activeOrganizationIdSubscription = this.activeOrganizationId$.pipe(
      skip(1),
      debounceTime(500),
      withLatestFrom(this.representativesRequestState$.pipe(
        filter((res) => res.loading === false),
      )),
    ).subscribe(() => {
      this.formGroup.patchValue({
        representativeId: parseInt(this.organizationService.representativeIdControl.value, 10),
        organizationId: parseInt(this.organizationService.organizationIdControl.value, 10),
      });
    });

    this.subscribers.activeOrganizationIdSubscription = this.openTransferCasesModalSubject$.pipe(
      switchMap(() => this.transferCases()),
    ).subscribe(() => {
      this.packagesService.clearActivePackage();
      this.selectedPackagesIds.patchValue([]);
      this.packagesService.getPackages(this.formGroup.getRawValue(), false);
    });

    this.subscribers.deleteLeadsSubscription = combineLatest([this.deleteSelectedLeads$, this.packageEntities$]).pipe(
      filter(([isDeleteLeads, packageEntities]) => !!isDeleteLeads && this.selectedPackagesIds.value.length),
      tap(([isDeleteLeads, packageEntities]) => this.deleteSelectedLeads$.next(false)),
      switchMap(([isDeleteLeads, packageEntities]) => this.deleteSelectedLeads(packageEntities)),
      withLatestFrom(this.organizationService.activeOrganizationId$),
      switchMap(([params, id]) => {
        if (params) {
          return this.packagesService.removeSelectedLeadPackages({
            packageIds: [...params],
            organizationId: id
          }).pipe(
            catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.modalService.showErrorModal(error.error.errors || [error.error]);
              }
              return EMPTY;
            }),
          );
        }
        return of({ deletedPackageIds: [] });
      })
    ).subscribe((data: any) => {
      if (!data.deletedPackageIds.length) {
        this.deSelectPackages$.next(PackageStatus.LEAD);
      } else {
        this.selectedPackagesIds.patchValue([]);
        this.packagesService.getPackages(this.formGroup.getRawValue(), false);
      }
      this.deleteSelectedLeads$.next(false);
      this.deSelectPackages$.next(null);
    });

    this.subscribers.unSelectedOpenPackagesSubscription = combineLatest([this.deSelectPackages$, this.packageEntities$]).pipe(
      filter(([deSelectPackageStatus, packageEntities]) => !!deSelectPackageStatus),
    ).subscribe(([deSelectPackageStatus, packageEntities]) => {
      const selectedPackageIds = this.filterSelectedPackages(deSelectPackageStatus, packageEntities);
      this.selectedPackagesIds.patchValue(selectedPackageIds);
      this.packagesService.getPackages(this.formGroup.getRawValue(), false);
    });

    this.subscribers.deleteTransfersSubscription = combineLatest([this.deleteSelectedTransfers$, this.packageEntities$]).pipe(
      filter(([isDeleteTransfers, packageEntities]) => !!isDeleteTransfers && this.selectedPackagesIds.value.length),
      switchMap(([isDeleteTransfers, packageEntities]) => this.deleteSelectedTransfers(packageEntities)),
      withLatestFrom(this.organizationService.activeOrganizationId$),
      switchMap(([params, id]) => {
        if (params) {
          return this.packagesService.removeSelectedTransferredPackages({
            packageIds: [...params],
            organizationId: id
          }).pipe(
            catchError((error: HttpErrorResponse) => {
              if (error.status !== 401) {
                this.modalService.showErrorModal(error.error.errors || [error.error]);
              }
              return EMPTY;
            }),
          );
        }
        return of({ deletedPackageIds: [] });
      })
    ).subscribe((data: any) => {
      if (!data.deletedPackageIds.length) {
        this.deSelectPackages$.next(PackageStatus.TRANSFERRED);
      } else {
        this.selectedPackagesIds.patchValue([]);
        this.packagesService.getPackages(this.formGroup.getRawValue(), false);
      }
      this.deleteSelectedTransfers$.next(false);
      this.deSelectPackages$.next(null);
    });
  }

  private filterSelectedPackages(packageStatus: PackageStatus, packageEntities) {
    const selectedPackageIds = this.selectedPackagesIds.value || [];
    const filteredPackageIds = selectedPackageIds.filter((selectedId) => {
      const aPackage = packageEntities[ selectedId ];
      return aPackage && aPackage.status === packageStatus;
    });
    return filteredPackageIds;
  }

  ngOnDestroy() {
    this.notificationsService.showComponent$.next(null);
  }

  createForm(data?) {
    this.formGroup = new FormGroup({
      sort: new FormControl('status'),
      benefitCategory: new FormControl(data && data.benefitCategory ? data.benefitCategory : null),
      countries: new FormControl(data && data.countries ? data.countries : null),
      closedDateStart: new FormControl(data && data.closedDateStart ? data.closedDateStart : null),
      closedDateEnd: new FormControl(data && data.closedDateEnd ? data.closedDateEnd : null),
      openedDateStart: new FormControl(data && data.openedDateStart ? data.openedDateStart : null),
      openedDateEnd: new FormControl(data && data.openedDateEnd ? data.openedDateEnd : null),
      lastAnsweredOnDateStart: new FormControl(data && data.lastAnsweredOnDateStart ? data.lastAnsweredOnDateStart : null),
      lastAnsweredOnDateEnd: new FormControl(data && data.lastAnsweredOnDateEnd ? data.lastAnsweredOnDateEnd : null),
      easyVisaId: new FormControl(data && data.easyVisaId ? data.easyVisaId : null),
      isOwed: new FormControl(data && data.isOwed ? data.isOwed : null),
      lastName: new FormControl(data && data.lastName ? data.lastName : null),
      max: new FormControl(data && data.max ? data.max : 25),
      mobileNumber: new FormControl(data && data.mobileNumber ? data.mobileNumber : null),
      offset: new FormControl(data && data.offset ? data.offset : 0),
      order: new FormControl('asc'),
      petitionerStatus: new FormControl(data && data.petitionerStatus ? data.petitionerStatus : null),
      representativeId: new FormControl(),
      organizationId: new FormControl(),
      status: new FormControl(data && data.status ? data.status : this.defaultStatusFilterValue),
      states: new FormControl(data && data.states ? data.states : null),
    });
  }

  addQueryParamsToUrl(params?) {
    this.router.navigate(['./'], {
      relativeTo: this.activatedRoute,
      queryParams: {
        ...params,
      }
    });
  }

  sortBy(colName) {
    if (colName !== this.sortFormControl.value) {
      this.formGroup.patchValue({ sort: colName, offset: 0 });
    } else {
      this.formGroup.patchValue({ order: this.orderFormControl.value === 'asc' ? 'desc' : 'asc', offset: 0 });
    }
  }

  pageChange(page) {
    const offset = (page - 1) * this.maxFormControl.value;
    if (offset !== (this.offsetFormControl.value)) {
      this.offsetFormControl.patchValue(offset);
    }
  }

  openSearchModal() {
    const modalRef = this.ngbModal.open(ClientSearchModalComponent, {
      windowClass: 'custom-modal-lg',
      centered: true,
    });
    return fromPromise(modalRef.result);
  }

  openDeleteOldLeadsModal() {
    const modalRef = this.ngbModal.open(DeleteOldLeadsModalComponent, {
      size: 'lg',
      centered: true,
    });
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY)
    );
  }

  searchForClients() {
    this.searchForClientsSubject$.next(true);
  }

  openChangeStatusModal(item) {
    this.changeCaseStatusSubject$.next(item.id);
  }

  setActivePackage(item?) {
    this.setActivePackageSubject$.next(item ? item.id : null);
  }

  deleteOldLeadPackages() {
    this.deleteLeadsByDateRange$.next();
  }

  showAllClients() {
    this.formGroup.patchValue({ status: this.allStatusFilterValue });
  }

  resetSearchFilters() {
    this.formGroup.patchValue(new SearchParams({}));
  }

  showOnlyDefaultClients() {
    this.formGroup.patchValue({
      status: this.defaultStatusFilterValue,
      offset: 0,
    });
  }

  getRepresentativeNameData(currentPackage, returnStyle = false) {
    const orderedAssignedRepresentative = orderBy(currentPackage.assignees, 'startDate', 'desc');
    const lastRepresentative = head(orderedAssignedRepresentative);
    const currentRepName = `${currentPackage.representative.lastName}, ${currentPackage.representative.firstName}`;
    if (returnStyle) {
      return lastRepresentative && lastRepresentative.status === 'Inactive' ? 'text-danger' : '';
    }
    if (!!(currentPackage && currentPackage.representativeId)) {
      return currentRepName;
    } else if (!!lastRepresentative) {
      return `${lastRepresentative.lastName}, ${lastRepresentative.firstName}  ${lastRepresentative.status === 'Inactive' ? '(Inactive)'
        : ''}`;
    } else {
      return currentRepName;
    }
  }

  transferCases() {
    let modalRef: NgbModalRef;
    if (!this.selectedPackagesIds.value.length || !this.representativeIdFormControl.value) {
      modalRef = this.ngbModal.open(CannotTransferModalComponent, { centered: true });
      modalRef.componentInstance.representativeId = this.representativeIdFormControl.value;
    } else {
      modalRef = this.ngbModal.open(TransferCasesModalComponent, {
        size: 'lg',
        windowClass: 'custom-modal-lg transfer-cases-modal',
      });
      modalRef.componentInstance.selectedPackagesIds = this.selectedPackagesIds.value;
    }
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  openTransferCasesModal() {
    this.openTransferCasesModalSubject$.next(true);
  }

  deleteLeads() {
    this.deleteSelectedLeads$.next(true);
  }

  deleteSelectedLeads(packageEntities) {
    let modalRef: NgbModalRef;
    const selectedPackageIds = this.selectedPackagesIds.value || [];
    const hasLeadPackages = every(selectedPackageIds, (selectedId) => {
      const aPackage = packageEntities[ selectedId ];
      return aPackage && aPackage.status === PackageStatus.LEAD;
    });
    if (!hasLeadPackages) {
      modalRef = this.ngbModal.open(CannotDeletePackagesModalComponent, {
        size: 'lg',
        centered: true,
        keyboard: false,
        backdrop: 'static'
      });
    } else {
      modalRef = this.ngbModal.open(ConfirmDeletePackagesModalComponent, {
        size: 'lg',
        centered: true
      });
      modalRef.componentInstance.selectedPackageIds = selectedPackageIds;
    }
    modalRef.componentInstance.packageStatus = PackageStatus.LEAD;
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  hasDisableDeleteLeads() {
    return this.packageEntities$.pipe(map((packageEntities) => {
      const selectedPackageIds = this.selectedPackagesIds.value;
      const leadPackageIds = selectedPackageIds.filter((selectedId) => {
        const aPackage = packageEntities[ selectedId ];
        return aPackage && aPackage.status === PackageStatus.LEAD;
      });
      return !selectedPackageIds.length || !leadPackageIds.length;
    }));
  }


  deleteTransfers() {
    this.deleteSelectedTransfers$.next(true);
  }

  deleteSelectedTransfers(packageEntities) {
    let modalRef: NgbModalRef;
    const selectedPackageIds = this.selectedPackagesIds.value || [];
    const hasTransferPackages = every(selectedPackageIds, (selectedId) => {
      const aPackage = packageEntities[ selectedId ];
      return aPackage && aPackage.status === PackageStatus.TRANSFERRED;
    });
    if (!hasTransferPackages) {
      modalRef = this.ngbModal.open(CannotDeletePackagesModalComponent, {
        size: 'lg',
        centered: true,
        keyboard: false,
        backdrop: 'static'
      });
    } else {
      modalRef = this.ngbModal.open(ConfirmDeletePackagesModalComponent, {
        size: 'lg',
        centered: true
      });
      modalRef.componentInstance.selectedPackageIds = selectedPackageIds;
    }
    modalRef.componentInstance.packageStatus = PackageStatus.TRANSFERRED;
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  hasDisableDeleteTransfers() {
    return this.packageEntities$.pipe(map((packageEntities) => {
      const selectedPackageIds = this.selectedPackagesIds.value;
      const transferredPackageIds = selectedPackageIds.filter((selectedId) => {
        const aPackage = packageEntities[ selectedId ];
        return aPackage && aPackage.status === PackageStatus.TRANSFERRED;
      });
      return !selectedPackageIds.length || !transferredPackageIds.length;
    }));
  }
}
