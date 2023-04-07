import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup } from '@angular/forms';
import { DatePipe } from '@angular/common';

import { Dictionary } from '@ngrx/entity';
import { BehaviorSubject, combineLatest, EMPTY, merge, Observable, Subject } from 'rxjs';
import { catchError, filter, map, pluck, skip, switchMap, take, withLatestFrom } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { fromPromise } from 'rxjs/internal-compatibility';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { TableDataFormat } from '../../../task-queue/models/table-data-format.model';
import { OrganizationService, UserService } from '../../../core/services';
import { FindLabelPipe } from '../../../shared/pipes/find-label/find-label.pipe';
import { User } from '../../../core/models/user.model';
import { RequestState } from '../../../core/ngrx/utils';

import { PermissionsService } from '../permissions.service';
import { OrganizationEmployee } from '../models/organization-employee.model';
import { EmployeePosition } from '../models/employee-position.enum';
import { employeeStatus } from '../models/employee-status.model';
import { EmployeeStatusValues } from '../models/employee-status.enum';
import { PermissionsLevelModalComponent } from '../modals/permissions-level-modal/permissions-level-modal.component';
import { OrganizationType } from '../../../core/models/organization-type.enum';
import { Attorney } from '../../../core/models/attorney.model';
import { RepresentativeType } from '../../../core/models/representativeType.enum';
import { AttorneyType } from '../../../core/models/attorney-type.enum';
import { Organization } from '../../../core/models/organization.model';


export interface PermissionsTableData {
  id: number;
  admin: FormControl;
  name: TableDataFormat;
  partner: TableDataFormat;
  attorney: TableDataFormat;
  manager: TableDataFormat;
  employee: TableDataFormat;
  trainee: TableDataFormat;
  activeDate: TableDataFormat;
  inactiveDate?: TableDataFormat;
  mobilePhone: TableDataFormat;
  status: TableDataFormat;
  active?: boolean;
}

@Component({
  selector: 'app-permissions-table',
  templateUrl: './permissions-table.component.html',
})
@DestroySubscribers()
export class PermissionsTableComponent implements OnInit, AddSubscribers, OnDestroy {
  permissions$: Observable<OrganizationEmployee[]>;
  permissionsEntities$: Observable<Dictionary<OrganizationEmployee>>;
  permissionsTableData$: Observable<PermissionsTableData[]>;
  inviteButtonLabel$: Observable<string>;
  currentUser$: Observable<User>;
  isAdmin$: Observable<boolean>;
  setEditUserIdSubject$: Subject<number> = new Subject<number>();
  setPendingUserIdSubject$: BehaviorSubject<number> = new BehaviorSubject<number>(null);
  withDrawInviteSubject$: Subject<boolean> = new Subject<boolean>();
  inviteDeleteState$: Observable<RequestState<any>>;
  showWithdrawButton$: Observable<boolean>;
  representativeType$: Observable<string> = new Observable<string>();
  activeOrganization$: Observable<Organization>;

  formGroup: FormGroup;
  adminIdFormControl: FormControl;

  EmployeePosition = EmployeePosition;
  headers = [
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.ADMIN',
      colName: 'admin',
      colClass: 'width-5 text-center px-1 pt-1 pb-1',
      hideHeader: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.NAME',
      colName: 'name',
      sortBy: true,
      colClass: 'width-10 px-1 pt-1 pb-1',
      hideHeader: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.PARTNER_OWNER_HEADER',
      colName: 'partner',
      colClass: 'text-center width-5 px-1 pt-1 pb-1',
      smallHeader: true,
      bgMiddleBlue: true,
      textWrap: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.ATTORNEY',
      colName: 'attorney',
      colClass: 'text-center width-5 px-1 pt-1 pb-1',
      smallHeader: true,
      bgMiddleBlue: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.MANAGER',
      colName: 'manager',
      colClass: 'text-center width-5 px-1 pt-1 pb-1',
      smallHeader: true,
      bgMiddleBlue: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.EMPLOYEE',
      colName: 'employee',
      colClass: 'text-center width-5 px-1 pt-1 pb-1',
      smallHeader: true,
      bgMiddleBlue: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.TRAINEE',
      colName: 'trainee',
      colClass: 'text-center width-5 px-1 pt-1 pb-1',
      smallHeader: true,
      bgMiddleBlue: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.ACCESS_DATE',
      colName: 'activeDate',
      colClass: 'text-center width-10 px-1 pt-1 pb-1',
      smallHeader: true,
      bgMiddleBlue: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.INACTIVATION_DATE',
      colName: 'inactiveDate',
      colClass: 'text-center width-10 text-wrap px-1 pt-1 pb-1',
      smallHeader: true,
      bgMiddleBlue: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.MOBILE_PHONE',
      colName: 'mobilePhone',
      colClass: 'text-center width-10 px-1 pt-1 pb-1',
      smallHeader: true,
      bgMiddleBlue: true
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.STATUS',
      colName: 'status',
      sortBy: true,
      colClass: 'text-center width-5 px-1 pt-1 pb-1',
      smallHeader: true,
      hideHeader: true
    },
  ];
  overHeaders = [
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.ADMIN',
      colName: 'head',
      colClass: 'width-5 px-1 pt-1 pb-1 mt-1',
      smallHeader: true,
      rowSpan: 2
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.NAME',
      sortColBy: 'name',
      colName: 'head',
      colClass: 'width-10 px-1 pt-1 pb-1',
      smallHeader: true,
      rowSpan: 2
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.PERMISSION_LEVEL',
      colName: 'head',
      colClass: 'text-center width-25  border-bottom-0 bg-dark-blue px-1 pt-1 pb-1',
      colSpan: 5
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.ACTIVE_DATE',
      colName: 'head',
      colClass: 'text-center width-30  border-bottom-0 bg-dark-blue px-1 pt-1 pb-1',
      colSpan: 3
    },
    {
      title: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.TABLE.STATUS',
      sortColBy: 'status',
      colName: 'head',
      colClass: 'text-center width-5  text-wrap px-1 pt-1 pb-1',
      smallHeader: true,
      rowSpan: 2
    },
  ];
  private subscribers: any = {};

  constructor(
    private permissionsService: PermissionsService,
    private organizationService: OrganizationService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private datePipe: DatePipe,
    private findLabel: FindLabelPipe,
    private userService: UserService,
    private ngbModal: NgbModal,
  ) {
    this.createFormGroup();
  }

  get includeAllFormControl() {
    return this.formGroup.get('includeAll');
  }

  get sortFormControl() {
    return this.formGroup.get('sort');
  }

  get orderFormControl() {
    return this.formGroup.get('order');
  }

  ngOnInit() {
    this.inviteDeleteState$ = this.permissionsService.inviteDeleteState$;
    this.permissions$ = this.permissionsService.permissions$;
    this.isAdmin$ = this.organizationService.isAdmin$;
    this.currentUser$ = this.userService.currentUser$;
    this.permissionsEntities$ = this.permissionsService.permissionsEntities$;
    this.activeOrganization$ = this.organizationService.activeOrganization$;

    this.adminIdFormControl = new FormControl(null);

    this.permissionsTableData$ = combineLatest([
      this.permissions$,
      this.isAdmin$,
      this.setPendingUserIdSubject$
    ]).pipe(
      map(([permissions, isAdmin, pendingUserId]) => permissions.map((permission) => ({
          id: permission.employeeId,
          admin: new FormControl({ value: permission.isAdmin, disabled: true }),
          name:
            {
              data: `${permission.profile.lastName},
               ${permission.profile.middleName ? permission.profile.middleName : ''}
               ${permission.profile.firstName}`,
              class: this.setInactiveClass(permission.status)
            },
          partner:
            {
              data: '',
              class: this.setCheckLevel(permission.position === EmployeePosition.PARTNER, isAdmin)
            },
          attorney:
            {
              data: '',
              class: this.setCheckLevel(permission.position === EmployeePosition.ATTORNEY, isAdmin)
            },
          manager:
            {
              data: '',
              class: this.setCheckLevel(permission.position === EmployeePosition.MANAGER, isAdmin)
            },
          employee:
            {
              data: '',
              class: this.setCheckLevel(permission.position === EmployeePosition.EMPLOYEE, isAdmin)
            },
          trainee:
            {
              data: '',
              class: this.setCheckLevel(permission.position === EmployeePosition.TRAINEE, isAdmin)
            },
          activeDate:
            {
              data: this.datePipe.transform(new Date(permission.activeDate), 'MM/dd/yyyy'),
              class: this.setInactiveClass(permission.status)
            },
          inactiveDate:
            {
              data: permission.inactiveDate ? this.datePipe.transform(new Date(permission.inactiveDate), 'MM/dd/yyyy') : null,
              class: this.setInactiveClass(permission.status)
            },
          mobilePhone: { data: permission.mobilePhone, class: this.setInactiveClass(permission.status) },
          status: {
            data: this.findLabel.transform(permission.status, employeeStatus),
            class: this.setInactiveClass(permission.status)
          },
          active: pendingUserId && pendingUserId == permission.employeeId,
          pointer: permission.status != EmployeeStatusValues.PENDING
        })))
    );

    this.inviteButtonLabel$ = this.organizationService.activeOrganization$.pipe(
      filter((activeOrganization) => !!activeOrganization),
      pluck('organizationType'),
      map((organizationType: OrganizationType) => organizationType === OrganizationType.SOLO_PRACTICE ?
        'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.INVITE_EXISTING_EV_MEMBER' : 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.INVITE_ATTORNEY'
      )
    );

    this.showWithdrawButton$ = merge(
      this.setPendingUserIdSubject$.pipe(
        map(value => !!value)
      ),
      this.inviteDeleteState$.pipe(
        map(res => !res.loaded && res.status === 'success')
      ),
      this.includeAllFormControl.valueChanges.pipe(
        filter((value) => !value)
      )
    );

    this.representativeType$ = combineLatest([
        this.currentUser$,
        this.activeOrganization$
      ]).pipe(
      filter(([user, activeOrganization]) => !!user && !!activeOrganization),
      map(([user, activeOrganization]) => {
        const attorney: Attorney = user.profile as Attorney;
        if (attorney.attorneyType == AttorneyType.SOLO_PRACTITIONER
          || activeOrganization.organizationType == OrganizationType.SOLO_PRACTICE) {
          return 'TEMPLATE.REPRESENTATIVE_TYPES.MEMBER';
        }
        if (attorney.attorneyType == AttorneyType.MEMBER_OF_A_LAW_FIRM ||
          activeOrganization.organizationType == OrganizationType.LAW_FIRM) {
          return 'TEMPLATE.REPRESENTATIVE_TYPES.MEMBER';
        }
        switch (attorney.representativeType) {
          case RepresentativeType.ATTORNEY: {
            return 'TEMPLATE.REPRESENTATIVE_TYPES.ATTORNEY';
          }
          case RepresentativeType.ACCREDITED_REPRESENTATIVE: {
            return 'TEMPLATE.REPRESENTATIVE_TYPES.ACCREDITED_REPRESENTATIVE';
          }
          default: {
            return 'TEMPLATE.REPRESENTATIVE_TYPES.MEMBER';
          }
        }
      })
    );
  }

  addSubscribers() {
    this.subscribers.queryParamsSubscription = this.activatedRoute.queryParams.pipe(
      filter((params) => !!params),
      take(1),
    ).subscribe((res) =>
      this.formGroup.patchValue({
        ...res,
        includeAll: !(res.includeAll === 'false' || res.includeAll === undefined)
      }, { emitEvent: false })
    );

    this.subscribers.getPermissionsFormGroupSubscription = this.formGroup.valueChanges.pipe(
      withLatestFrom(this.organizationService.activeOrganizationId$),
      switchMap(([, organizationId]) =>
        this.permissionsService.getPermissions({
          params: this.formGroup.getRawValue(),
          organizationId
        }).pipe(
          catchError(() => EMPTY),
          take(1),
        )),
    ).subscribe(() => this.addQueryParamsToUrl(this.formGroup.getRawValue()));

    this.subscribers.activeOrganizationIdSubscription = this.organizationService.activeOrganizationId$.pipe(
      skip(1),
      filter((id) => !!id),
      // TODO use skip for ignore first emit of activeOrganizationId
    ).subscribe(() => this.resetFormGroup());

    this.subscribers.setEditUserIdSubscription = this.setEditUserIdSubject$.pipe(
      filter((id) => !!id),
      switchMap((id) => this.permissionsEntities$.pipe(
        map((permissionsEntities) => permissionsEntities[ id ])
      )),
      filter((id) => !!id),
      withLatestFrom(
        this.isAdmin$,
        this.organizationService.currentPosition$
      ),
    ).subscribe(([permission, isAdmin, position]) => {
        const validPositions = [EmployeePosition.PARTNER, EmployeePosition.ATTORNEY];
        const isValidPosition = validPositions.some(validPosition => validPosition === position);
        if (isAdmin && (permission.status !== EmployeeStatusValues.PENDING)) {
          this.setPendingUserIdSubject$.next(null);
          this.router.navigate([permission.employeeId, 'edit-user'], { relativeTo: this.activatedRoute });
        } else if (permission.status === EmployeeStatusValues.PENDING && isValidPosition || isAdmin) {
          this.setPendingUserIdSubject$.next(permission.employeeId);
        }
      }
    );

    this.subscribers.withdrawInviteSubscription = this.withDrawInviteSubject$.pipe(
      filter((res) => !!res),
      withLatestFrom(this.setPendingUserIdSubject$),
    ).subscribe(([, id]) => this.openWithdrawInviteAction(id));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  addQueryParamsToUrl(params?) {
    this.router.navigate(['./'], { relativeTo: this.activatedRoute, queryParams: { ...params } });
  }

  sortBy(colName) {
    if (colName !== this.sortFormControl.value) {
      this.formGroup.patchValue({ sort: colName });
    } else {
      this.formGroup.patchValue({ order: this.orderFormControl.value === 'asc' ? 'desc' : 'asc' });
    }
  }

  setCheckLevel(level, isAdmin) {
    const iconType = level ? 'fa fa-check' : 'fa fa-close';
    let iconStyle = '';

    if (isAdmin) {
      iconStyle = level ? `${iconType} text-primary` : `${iconType} text-danger`;
    } else {
      iconStyle = `${iconType} text-gray`;
    }
    return iconStyle;
  }

  setInactiveClass(status) {
    return status === EmployeeStatusValues.INACTIVE ? 'text-danger' : '';
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      sort: new FormControl('name'),
      order: new FormControl('asc'),
      includeAll: new FormControl(),
    });
  }

  resetFormGroup(data = {
    sort: 'name',
    order: 'asc',
    includeAll: false
  }) {
    this.formGroup.reset({
      sort: data.sort,
      order: data.order,
      includeAll: data.includeAll,
    });
  }

  setEditUserId(item) {
    this.setEditUserIdSubject$.next(item.id);
  }

  openPermissionsLevelModal() {
    const modalRef = this.ngbModal.open(PermissionsLevelModalComponent, {
      windowClass: 'custom-modal-lg',
      centered: true,
    });
    modalRef.componentInstance.isRequest = false;
    return fromPromise(modalRef.result);
  }

  openWithdrawInviteAction(data) {
    this.permissionsService.openWithdrawInviteModalAction(data);
    this.setEditUserIdSubject$.next(null);
  }

  withdrawInvite() {
    this.withDrawInviteSubject$.next(true);
  }
}
