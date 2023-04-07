import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { BehaviorSubject, combineLatest, Observable, ConnectableObservable, Subject } from 'rxjs';
import {
  filter,
  map,
  pluck,
  publishReplay,
  refCount,
  startWith,
  switchMap,
  take
} from 'rxjs/operators';

import { isEqual } from 'lodash-es';

import { ConfigDataService } from '../../../core/services';
import { FeeDetails } from '../../../core/models/fee-details.model';

import { DashboardSettingsService } from '../settings.service';

@Component({
  selector: 'app-customer-fees-discounts',
  templateUrl: './customer-fees-discounts.component.html',
  styleUrls: ['./customer-fees-discounts.component.scss']
})
@DestroySubscribers()
export class CustomerFeesDiscountsComponent implements OnInit, OnDestroy, AddSubscribers {

  form: FormGroup;

  resetForm$ = new BehaviorSubject<void>(null);

  submitForm$ = new Subject<FeeDetails>();

  isEqualData$: Observable<boolean>;

  isFeeDetailsPostLoading$: Observable<boolean>;

  private feeDetails$: Observable<FeeDetails>;

  private subscribers: any = {};

  constructor(
    private configDataService: ConfigDataService,
    private dashboardSettingsService: DashboardSettingsService,
  ) {
    this.createForm();
  }

  get formValue$() {
    return this.form.valueChanges.pipe(
      startWith(this.form.value),
      publishReplay(1),
      refCount(),
    );
  }

  ngOnInit() {
    this.feeDetails$ = this.configDataService.feeDetails$.pipe(
      filter((feeDetails) => !!feeDetails)
    );

    this.isEqualData$ = combineLatest([
      this.feeDetails$,
      this.formValue$
    ]).pipe(
      map(([feeDetails, formValue]) => isEqual(feeDetails, formValue)),
      publishReplay(1),
    );

    this.isFeeDetailsPostLoading$ = this.dashboardSettingsService.feeDetailsPostState$.pipe(
      pluck('loading')
    );
  }

  addSubscribers() {
    this.subscribers.isCancelDisableSubscription = (this.isEqualData$ as ConnectableObservable<boolean>).connect();

    this.subscribers.resetFormSubscription = this.resetForm$.pipe(
      switchMap(() =>
        this.feeDetails$.pipe(
          take(1)
        )
      )
    ).subscribe((feeDetails) => {
      this.resetFormGroup(feeDetails);
    });

    this.subscribers.submitSubscription = this.submitForm$.pipe(
      filter(() => this.form.valid),
    ).subscribe((value) => {
      this.dashboardSettingsService.updateFeeDetails(value);
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  onSubmit() {
    this.submitForm$.next(this.form.value);
  }

  resetForm() {
    this.resetForm$.next(null);
  }

  private resetFormGroup(data: FeeDetails) {
    this.form.reset(data);
  }

  private createForm() {
    this.form = new FormGroup({
      signupFee: new FormControl(null, [Validators.required, Validators.min(0)]),
      maintenanceFee: new FormControl(null, [Validators.required, Validators.min(0)]),
      cloudStorageFee: new FormControl(null, [Validators.required, Validators.min(0)]),
      membershipReactivationFee: new FormControl(null, [Validators.required, Validators.min(0)]),
      referralBonus: new FormControl(null, [Validators.required, Validators.min(0)]),
      signupDiscount: new FormControl(null, [Validators.required, Validators.min(0)]),
      articleBonus: new FormControl(null, [Validators.required, Validators.min(0)]),
      supportEmail: new FormControl(null, [Validators.email]),
      contactPhone: new FormControl(null),
    });
  }

}
