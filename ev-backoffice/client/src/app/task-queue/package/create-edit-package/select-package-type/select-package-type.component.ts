import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { EMPTY, merge, Observable, of, Subject } from 'rxjs';
import {
  catchError,
  filter,
  map,
  mapTo,
  pluck,
  publishReplay,
  refCount,
  switchMap,
  switchMapTo,
  take,
  withLatestFrom
} from 'rxjs/operators';

import { head } from 'lodash-es';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { ModalService, OrganizationService, PackagesService, UserService } from '../../../../core/services';
import { RequestState } from '../../../../core/ngrx/utils';
import { User } from '../../../../core/models/user.model';
import { OrganizationType } from '../../../../core/models/organization-type.enum';
import { Package } from '../../../../core/models/package/package.model';
import { PackageStatus } from '../../../../core/models/package/package-status.enum';

import { PackageType } from '../../../models/package-type.enum';

import { CreateApplicantFormGroupService } from '../../services';

import { EVIdEmailValidator } from '../validators/evid-email.validator';

@Component({
  selector: 'app-select-package-type',
  templateUrl: './select-package-type.component.html',
  styleUrls: [ './select-package-type.component.scss' ],
})

@DestroySubscribers()
export class SelectPackageTypeComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() packageType: FormControl;

  packageTypeConstants = {
    EDIT: PackageType.EDIT,
    NEW: PackageType.NEW
  };

  editedPackage$: Observable<Package>;
  package$: Observable<Package>;
  packages$: Observable<Package[]>;
  foundPackages$: Observable<Package[]>;
  getPackagesRequest$: Observable<RequestState<Package[]>>;
  activeOrganizationId$: Observable<string>;
  currentUser$: Observable<User> = this.userService.currentUser$;
  isShowWarning$: Observable<boolean>;

  organizationType$: Observable<OrganizationType>;

  searchPackageFormGroup: FormGroup;
  packageFormControl = new FormControl('');

  statusList = [
    PackageStatus.LEAD,
    PackageStatus.OPEN,
    PackageStatus.CLOSED,
    PackageStatus.BLOCKED,
    PackageStatus.TRANSFERRED
  ];

  private searchPackagesSubject$: Subject<string> = new Subject<string>();
  private subscribers: any = {};

  constructor(
    private modalService: ModalService,
    private packagesService: PackagesService,
    private organizationService: OrganizationService,
    private userService: UserService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
  ) {
  }

  get isEdit() {
    return this.packageType.value === this.packageTypeConstants.EDIT;
  }

  get EVIdFormControl() {
    return this.searchPackageFormGroup.get('EVId');
  }

  get formGroup() {
    return this.createApplicantFormGroupService.formGroup;
  }

  ngOnInit() {
    this.activeOrganizationId$ = this.organizationService.activeOrganizationId$;
    this.searchPackageFormGroup = new FormGroup({
      EVId: new FormControl(null, [
        Validators.required,
        EVIdEmailValidator
      ]),
    });
    this.package$ = this.packagesService.package$;
    this.getPackagesRequest$ = this.packagesService.getPackagesRequest$;
    this.organizationType$ = this.organizationService.activeOrganization$.pipe(
      filter(organization => !!organization),
      pluck('organizationType')
    );

    this.foundPackages$ = this.searchPackagesSubject$.pipe(
      filter(() => this.searchPackageFormGroup.valid),
      withLatestFrom(this.activeOrganizationId$),
      switchMap(([ EVId, id ]) =>
        this.packagesService.getPackages({
            search: EVId,
            sort: 'creationDate',
            order: 'desc',
            organizationId: id,
            status: this.statusList
          },
          true,
          true
        ).pipe(
          catchError(() => EMPTY),
          pluck('body'),
          map((packages: Package[]) =>
            packages
          ),
          take(1)
        )
      ),
      publishReplay(1),
      refCount(),
    );

    this.editedPackage$ = this.activatedRoute.params.pipe(
      filter(params => params.id),
      switchMapTo(this.package$.pipe(
        filter(value => !!value),
      )),
    );

    this.packages$ = merge(
      this.foundPackages$,
      this.editedPackage$.pipe(
        map(currentPackage => [ currentPackage ])
      ),
    );

    this.isShowWarning$ = this.searchPackagesSubject$.pipe(
      filter(res => !!res),
      switchMap(() => merge(
        this.EVIdFormControl.valueChanges.pipe(mapTo(false)),
        this.packageType.valueChanges.pipe(mapTo(false)),
        of((this.EVIdFormControl.hasError('invalid') || this.EVIdFormControl.hasError('required')))
      )));
  }

  addSubscribers() {
    this.subscribers.formGroupSubscription = this.formGroup.valueChanges
      .subscribe(() => {
        this.packageType.disable({ emitEvent: false });
      });

    this.subscribers.foundPackagesSubscription = this.foundPackages$
      .subscribe((packages) => {
        if (packages && packages.length === 1) {
          const item = head(packages);
          this.packageFormControl.patchValue(item.id);
        } else {
          this.packageFormControl.patchValue('');
          this.packageFormControl.enable();
        }
      });

    this.subscribers.selectPackageSubscription = this.packageFormControl.valueChanges.pipe(
      filter((value) => !!value),
      take(1),
    )
      .subscribe(packageId => {
        this.packagesService.selectPackage(packageId);
        this.router.navigate([ 'task-queue', 'package', packageId, 'edit' ]);
      });

    this.subscribers.packageSubscription = this.editedPackage$
      .subscribe(currentPackage => {
        this.packageFormControl.patchValue(currentPackage.id);
        this.packageFormControl.disable({ emitEvent: false });
        this.searchPackageFormGroup.disable({ emitEvent: false });
      });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  onSubmit() {
    this.searchPackagesSubject$.next(this.searchPackageFormGroup.value.EVId);
  }
}
