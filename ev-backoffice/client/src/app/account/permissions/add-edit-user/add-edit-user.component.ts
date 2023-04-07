import { Component, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, filter, map, pluck, switchMap, take, throttleTime, withLatestFrom } from 'rxjs/operators';
import { combineLatest, EMPTY, Observable, Subject } from 'rxjs';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { Attorney } from '../../../core/models/attorney.model';
import { User } from '../../../core/models/user.model';
import { AuthService, ModalService, OrganizationService, UserService } from '../../../core/services';
import { RepresentativeType } from '../../../core/models/representativeType.enum';
import { AsyncRequestValidator } from '../../../shared/validators/async-request.validator';
import { SignUpService } from '../../../auth/services';
import { ConfirmButtonType } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { Organization } from '../../../core/models/organization.model';
import { RequestState } from '../../../core/ngrx/utils';
import { rolesHasAccess } from '../../../shared/utils/roles-has-access';
import { Role } from '../../../core/models/role.enum';

import { EmployeePosition } from '../models/employee-position.enum';
import { PermissionsService } from '../permissions.service';
import { OrganizationEmployee } from '../models/organization-employee.model';
import { EmployeeStatusValues } from '../models/employee-status.enum';


@Component({
  selector: 'app-add-edit-user',
  templateUrl: './add-edit-user.component.html',
})
@DestroySubscribers()
export class AddEditUserComponent implements OnInit, AddSubscribers {
  @ViewChild('permissionsLevel', { static: true }) permissionsLevel;
  @ViewChild('lawPracticeWillBeDeleted', { static: true }) lawPracticeWillBeDeleted;

  activeOrganizationId$: Observable<string>;
  employeeStatus$: Observable<EmployeeStatusValues>;
  currentUser$: Observable<User>;
  isAccreditedRepresentative$: Observable<boolean>;
  employeeId$: Observable<number>;
  isAdmin$: Observable<boolean>;
  disableChangePosition$: Observable<boolean>;
  disableChangeAdmin$: Observable<boolean>;
  isInactive$: Observable<boolean>;
  addEditUserTitle$: Observable<any>;
  activeOrganizationEmployee$: Observable<OrganizationEmployee>;
  activeOrganizationEmployeeId$: Observable<number>;
  activeOrganization$: Observable<Organization>;
  getPermissionGetState$: Observable<RequestState<OrganizationEmployee>>;
  createEmployeeLoading$: Observable<boolean>;
  updateEmployeeLoading$: Observable<boolean>;
  addUserSubject$: Subject<any> = new Subject();
  editUserSubject$: Subject<any> = new Subject();
  formSubmitSubject$: Subject<any> = new Subject();
  isLawFirm$: Observable<boolean>;
  permissions$: Observable<OrganizationEmployee[]>;
  currentUserEasyVisaId$: Observable<string>;

  formGroup: FormGroup;

  private subscribers: any = {};

  employeePosition = EmployeePosition;
  employeeStatusValues = EmployeeStatusValues;


  constructor(
    private permissionService: PermissionsService,
    private activatedRoute: ActivatedRoute,
    private organizationService: OrganizationService,
    private userService: UserService,
    private authService: AuthService,
    private signUpService: SignUpService,
    private modalService: ModalService,
    private router: Router,
  ) {
    this.createFormGroup();
  }

  get firstFormControl() {
    return this.formGroup.get('firstName');
  }

  get middleFormControl() {
    return this.formGroup.get('middleName');
  }

  get lastFormControl() {
    return this.formGroup.get('lastName');
  }

  get emailControl() {
    return this.formGroup.get('email');
  }

  get mobilePhoneControl() {
    return this.formGroup.get('mobilePhone');
  }

  get officePhoneControl() {
    return this.formGroup.get('officePhone');
  }

  get positionControl() {
    return this.formGroup.get('position');
  }

  get statusControl() {
    return this.formGroup.get('status');
  }

  get isAdminControl() {
    return this.formGroup.get('isAdmin');
  }

  get isTrainee() {
    return this.positionControl.value === EmployeePosition.TRAINEE;
  }

  get isPartner() {
    return this.positionControl.value === EmployeePosition.PARTNER;
  }


  ngOnInit() {
    this.activeOrganizationId$ = this.organizationService.activeOrganizationId$;
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.currentUserEasyVisaId$ = this.userService.currentUserEasyVisaId$;
    this.activeOrganizationEmployeeId$ = this.permissionService.activePermissionId$;
    this.activeOrganizationEmployee$ = this.permissionService.activePermission$;
    this.currentUser$ = this.userService.currentUser$;
    this.isAdmin$ = this.organizationService.isAdmin$;
    this.getPermissionGetState$ = this.permissionService.getPermissionGetState$;
    this.permissions$ = this.permissionService.permissions$;
    this.createEmployeeLoading$ = this.permissionService.createEmployeePostState$.pipe(map(state => state.loading));
    this.updateEmployeeLoading$ = this.permissionService.updateEmployeePutState$.pipe(map(state => state.loading));

    this.employeeId$ = this.activatedRoute.params.pipe(
      map((params) => params['id'] || null)
    );

    this.disableChangePosition$ = this.activeOrganizationEmployee$.pipe(
      filter((employee) => !!employee),
      map(employee => employee.roles),
      map((roles) => rolesHasAccess(roles, [Role.ROLE_ATTORNEY, Role.ROLE_EV, Role.ROLE_OWNER])),
    );

    this.disableChangeAdmin$ = this.activeOrganizationEmployee$.pipe(
      filter((employee) => !!employee),
      map((employee) => employee.position === EmployeePosition.PARTNER),
    );

    this.employeeStatus$ = this.activeOrganizationEmployee$.pipe(
      filter((activeOrganizationEmployee) => !!activeOrganizationEmployee),
      map((currentPermission) => currentPermission.status),
    );

    this.isInactive$ = this.activeOrganizationEmployee$.pipe(
      filter((activeOrganizationEmployee) => !!activeOrganizationEmployee),
      map((currentPermission) => currentPermission.status === EmployeeStatusValues.INACTIVE),
    );

    this.isAccreditedRepresentative$ = this.currentUser$.pipe(
      filter((user) => !!user),
      pluck('profile'),
      map((user: Attorney) => user.representativeType === RepresentativeType.ACCREDITED_REPRESENTATIVE
      )
    );

    this.addEditUserTitle$ = this.activatedRoute.params.pipe(
      map((params) => params.id ?
          'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.ADD_EDIT_USER.HEADER_EDIT' : 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.ADD_EDIT_USER.HEADER')
    );
    this.isLawFirm$ = this.organizationService.isLawFirm$;
  }

  addSubscribers() {
    this.subscribers.formSubmitSubscription = this.formSubmitSubject$.pipe(
      switchMap(() => this.employeeId$),
      throttleTime(500),
    ).subscribe((id) => {
        id ? this.editUserSubject$.next(true) : this.addUserSubject$.next(true);
      }
    );

    this.subscribers.employeeIdSubscription = this.employeeId$
    .subscribe((id) => {
        if (id) {
          this.emailControl.disable({emitEvent: false});
          this.officePhoneControl.disable({emitEvent: false});
          this.mobilePhoneControl.disable({emitEvent: false});
          this.firstFormControl.disable({emitEvent: false});
          this.middleFormControl.disable({emitEvent: false});
          this.lastFormControl.disable({emitEvent: false});
        } else {
          this.isAdminControl.disable({emitEvent: false});
          this.statusControl.patchValue(EmployeeStatusValues.ACTIVE);
        }
      }
    );

    this.subscribers.addUserSubscription = this.addUserSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap(() => this.activeOrganizationId$),
      switchMap((organizationId) => this.permissionService.createEmployee((
        {
          organizationEmployee: this.formGroup.value,
          organizationId
        }
      )).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [error.error]);
          }
          return EMPTY;
        }),
        take(1),
      ))
    ).subscribe(() => this.router.navigate(['account', 'permissions']));

    this.subscribers.editUserSubscription = this.editUserSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap(() => combineLatest([
        this.employeeId$,
        this.activeOrganizationId$,
      ])),
      switchMap(([id, organizationId]) =>
        this.permissionService.updateEmployee((
          {
            employeeId: id,
            organizationId,
            organizationEmployee: this.formGroup.getRawValue(),
          })).pipe(
          catchError((error: HttpErrorResponse) => {
            if (error.status !== 401) {
              this.modalService.showErrorModal(error.error.errors || [error.error]);
            }
            return EMPTY;
          }),
          take(1),
        )
      ),
      withLatestFrom(
        this.userService.currentUserEasyVisaId$,
        this.authService.currentUserToken$
        ),
    ).subscribe(([res, evId, token]: [OrganizationEmployee, string, string]) => {
      if (res.profile.easyVisaId === evId) {
        this.userService.getUser(token);
      }
      this.router.navigate(['account', 'permissions']);
    });

    this.subscribers.activeRouteSubscription = this.activeOrganizationEmployee$.pipe(
      filter((activeOrganizationEmployee) => !!activeOrganizationEmployee)
    )
    .subscribe((activeOrganizationEmployee) => {
      if (activeOrganizationEmployee.status === EmployeeStatusValues.INACTIVE) {
        this.formGroup.disable({emitEvent: false});
      }
      this.resetCreateFormGroup(activeOrganizationEmployee);
    });

    this.subscribers.positionControlSubscription = this.positionControl.valueChanges.pipe(
    ).subscribe((position) => {
      if (position === EmployeePosition.TRAINEE) {
        this.isAdminControl.patchValue(false);
      } else if (position === EmployeePosition.PARTNER) {
        this.isAdminControl.patchValue(true);
      }
    });
  }

  createFormGroup(data?) {
    this.formGroup = new FormGroup({
      isAdmin: new FormControl(data ? data.isAdmin : null),
      firstName: new FormControl(data ? data.profile.firstName : null, Validators.required),
      middleName: new FormControl(data ? data.profile.middleName : null),
      lastName: new FormControl(data ? data.profile.lastName : null, Validators.required),
      email: new FormControl(data ? data.profile.email : null, {
        updateOn: 'change',
        validators: [
          Validators.required,
          Validators.email
        ],
        asyncValidators: AsyncRequestValidator.createValidator(
          (value) => this.signUpService.emailValidateRequest(value),
          data ? data.profile.email : null,
        ),
      }),
      mobilePhone: new FormControl(data ? data.mobilePhone : null),
      officePhone: new FormControl(data ? data.officePhone : null),
      position: new FormControl(data ? data.position : null),
      status: new FormControl(data ? data.status : null),
    });
  }

  private resetCreateFormGroup(data: OrganizationEmployee) {
    const res = data ? data.profile : {};
    return this.formGroup.reset({...data, ...res}, {emitEvent: false});
  }

  openPermissionsLevelNotAllowed() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.ADD_EDIT_USER.MODALS.HEADER',
      body: this.permissionsLevel,
      buttons,
      centered: true,
    }).pipe(
      catchError(() => EMPTY)
    );
  }

  openFirmWillBeDeletedPopUp() {
    const buttons = [
      {
        label: 'FORM.BUTTON.OK',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-2 min-w-100',
      },
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-2 min-w-100',
      }
    ];

    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PROFILE.PERMISSIONS.ADD_EDIT_USER.LAW_PRACTICE_ACCOUNT_WILL_BE_DELETED.HEADER',
      body: this.lawPracticeWillBeDeleted,
      size: 'lg',
      buttons,
      centered: true,
    }).pipe(
      catchError(() => EMPTY)
    );
  }
  formSubmit() {
    return this.formSubmitSubject$.next(true);
  }
}
