import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormControl } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { Observable } from 'rxjs';
import { filter, map, switchMapTo, withLatestFrom } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { PackageApplicant } from '../../../core/models/package/package-applicant.model';
import { OrganizationService, PackagesService, UserService } from '../../../core/services';
import { Package } from '../../../core/models/package/package.model';
import { PackageStatus, packageStatusValue } from '../../../core/models/package/package-status.enum';

import { PackageType } from '../../models/package-type.enum';

import { CreateApplicantFormGroupService } from '../services';

@Component({
  selector: 'app-create-package',
  templateUrl: './create-edit-package.component.html'
})
@DestroySubscribers()
export class CreateEditPackageComponent implements OnInit, OnDestroy, AddSubscribers {

  package$: Observable<Package>;
  packageId$: Observable<number>;
  isReadOnlyPackage$: Observable<boolean>;
  currentEditedPackage$: Observable<Package>;

  packageTypeFormControl = new FormControl('');

  private subscribers: any = {};

  PackageStatus = PackageStatus;
  PackageType = PackageType;
  packageStatusValue = packageStatusValue;

  constructor(
    private packagesService: PackagesService,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
    private userService: UserService,
    private organizationService: OrganizationService,
    private activatedRoute: ActivatedRoute,
  ) {
    this.createApplicantFormGroupService.createFormGroup();
  }

  get formGroup() {
    return this.createApplicantFormGroupService.formGroup;
  }

  get applicantsFormArray() {
    return this.formGroup.get('applicants') as FormArray;
  }

  get representativeIdFormControl() {
    return this.formGroup.get('representativeId');
  }

  ngOnInit() {
    this.package$ = this.packagesService.package$;

    this.packageId$ = this.activatedRoute.params.pipe(
      filter((params) => params.id),
      map(params => parseInt(params.id, 10)),
    );

    this.currentEditedPackage$ = this.packageId$.pipe(
      switchMapTo(this.package$.pipe(
        filter((currentPackage) => !!currentPackage)
        )
      )
    );

    this.isReadOnlyPackage$ = this.package$.pipe(
      filter((currentPackage) => !!currentPackage),
      map((currentPackage) => currentPackage.status == PackageStatus.TRANSFERRED)
    );

    this.createApplicantFormGroupService.canOut = false;
  }

  addSubscribers() {
    this.subscribers.activeRouteSubscription = this.packageId$.pipe(
      withLatestFrom(this.packagesService.activePackage$),
      filter(([paramsId, activePackage]) => !activePackage || activePackage.id !== paramsId),
    )
    .subscribe(([paramsId, ]) => this.packagesService.setActivePackage(paramsId));

    this.subscribers.packageTypeSubscription = this.packageTypeFormControl.valueChanges.pipe(
      filter((packageType) => packageType === 'NEW'),
    )
    .subscribe(() => {
      this.packagesService.setActivePackage(null);
      this.packagesService.removePackages()
      this.representativeIdFormControl.patchValue(
        this.organizationService.representativeIdControl.value,
        {emitEvent: false});
    });

    this.subscribers.packageSubscription = this.currentEditedPackage$
    .subscribe((currentPackage) => {
      this.packageTypeFormControl.patchValue('EDIT');
      this.packageTypeFormControl.disable();
      this.createApplicantFormGroupService.createFormGroup(currentPackage);
      this.representativeIdFormControl.patchValue(currentPackage.representativeId);
      this.representativeIdFormControl.disable();
      currentPackage.applicants.map((applicant: PackageApplicant) => {
        const applicantItem = this.createApplicantFormGroupService.createBeneficiaryFormGroup(applicant.applicantType, applicant);
        this.applicantsFormArray.push(applicantItem);
      });
    });

    this.subscribers.createPackageSnapshotSubscription = this.currentEditedPackage$
    .subscribe(() => {
      const {representativeId, ...valueToPatch} = this.formGroup.getRawValue();
      this.createApplicantFormGroupService.createFormGroupSnapShot(valueToPatch);
    });
  }

  onKeyPress(e, formElement){
    if (e.key === 'Enter') {
      const focusableList = Array.from(formElement.querySelectorAll('input,a,select,button,textarea')).filter((data: any) => !data.hidden && !data.disabled);
      const nextIndex = focusableList.indexOf(e.target) === focusableList.length - 1 ? 0 : focusableList.indexOf(e.target) + 1;
      const next: any = focusableList[ nextIndex ];
      next?.focus();
      return false;
    }
  }

  ngOnDestroy() {
    this.createApplicantFormGroupService.resetFormGroup();
  }
}
