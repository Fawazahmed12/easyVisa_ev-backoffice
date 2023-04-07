import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { find } from 'lodash-es';

import { Observable, Subject } from 'rxjs';
import { filter, map, startWith, switchMapTo, take, tap } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { Package } from '../../../../core/models/package/package.model';
import { FeeSchedule } from '../../../../core/models/fee-schedule.model';
import { OrganizationService, PackagesService } from '../../../../core/services';

import { CreateApplicantFormGroupService } from '../../services';
import { PackageApplicant } from '../../../../core/models/package/package-applicant.model';

@Component({
  selector: 'app-billing-info',
  templateUrl: './billing-info.component.html',
})
@DestroySubscribers()
export class BillingInfoComponent implements OnInit, OnDestroy, AddSubscribers {
  @Input() isReadOnlyPackage: Boolean = false;

  package$: Observable<Package>;
  editedPackage$: Observable<Package>;
  previousBalance$: Observable<number>;
  currentBalance$: Observable<number>;

  private resetFeesSubject$: Subject<boolean> = new Subject<boolean>();
  private feeSchedule$: Observable<FeeSchedule[]>;
  private subscribers: any = {};

  constructor(
    private activatedRoute: ActivatedRoute,
    private createApplicantFormGroupService: CreateApplicantFormGroupService,
    private packagesService: PackagesService,
    private organizationService: OrganizationService,
  ) {
  }

  get applicantsFormGroups() {
    return this.applicantsFormArray.controls as [FormGroup];
  }

  get applicantsFormArray() {
    return this.createApplicantFormGroupService.formGroup.get('applicants') as FormArray;
  }

  get representativeId() {
    return this.createApplicantFormGroupService.formGroup.get('representativeId').value;
  }

  ngOnInit() {
    this.package$ = this.packagesService.package$.pipe(
      filter(value => !!value),
    );
    this.editedPackage$ = this.activatedRoute.params.pipe(
      filter(params => params.id),
      switchMapTo(this.package$),
    );

    this.currentBalance$ = this.applicantsFormArray.valueChanges.pipe(
      startWith<{applicants: PackageApplicant }, PackageApplicant>(this.applicantsFormArray.value),
      map((value) => this.countBalance(value)),
    );

    this.previousBalance$ = this.package$.pipe(
      take(1),
      map(currentPackage => this.countBalance(currentPackage.applicants)),
    );

    this.feeSchedule$ = this.organizationService.currentRepresentativeFeeSchedule$;
  }

  addSubscribers() {
    this.subscribers.resetFeesSubscription = this.resetFeesSubject$.pipe(
      switchMapTo(this.feeSchedule$)
    )
    .subscribe((feeSchedule) =>
      this.applicantsFormGroups.forEach((formGroup) => {
        const defaultFee = find(feeSchedule, {benefitCategory: formGroup.value.benefitCategory});
        formGroup.get('fee').patchValue(defaultFee ? defaultFee.amount : 0);
      })
    );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  countBalance(applicantArray) {
    return applicantArray?.reduce((acc, applicant) => {
      acc += !!applicant?.fee ? +applicant.fee : 0;
      return acc;
    }, 0);
  }

  resetFees() {
    this.resetFeesSubject$.next(true);
  }
}
