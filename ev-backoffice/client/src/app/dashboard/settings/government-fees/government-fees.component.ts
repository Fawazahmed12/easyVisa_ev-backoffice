import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { BehaviorSubject, combineLatest, ConnectableObservable, EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, map, pluck, publishReplay, refCount, startWith, switchMap, switchMapTo } from 'rxjs/operators';

import { isEqual } from 'lodash-es';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { GovernmentFee } from '../../../core/models/government-fee.model';
import { ConfigDataService, ModalService } from '../../../core/services';

import { DashboardSettingsService } from '../settings.service';

@Component({
  selector: 'app-government-fees',
  templateUrl: './government-fees.component.html',
})
@DestroySubscribers()
export class GovernmentFeesComponent implements OnInit, AddSubscribers, OnDestroy {

  governmentFeesFormGroup: FormGroup;

  resetForm$ = new BehaviorSubject<void>(null);

  submitForm$ = new Subject<GovernmentFee>();

  isCancelButtonDisabled$: Observable<boolean>;

  isGovernmentFeePostLoading$: Observable<boolean>;

  private governmentFee$: Observable<GovernmentFee>;

  private subscribers: any = {};

  constructor(
    private configDataService: ConfigDataService,
    private dashboardSettingsService: DashboardSettingsService,
    private modalService: ModalService,
  ) {
    this.createGovernmentFeesFormGroup();
  }

  get formValue$() {
    return this.governmentFeesFormGroup.valueChanges.pipe(
      startWith(this.governmentFeesFormGroup.value),
      publishReplay(1),
      refCount(),
    );
  }

  ngOnInit() {
    this.governmentFee$ = this.configDataService.governmentFee$.pipe(
      filter((governmentFee) => !!governmentFee)
    );

    this.isCancelButtonDisabled$ = combineLatest([
      this.governmentFee$,
      this.formValue$
    ]).pipe(
      map(([feeDetails, formValue]) => isEqual(feeDetails, formValue)),
      publishReplay(1),
    );

    this.isGovernmentFeePostLoading$ = this.dashboardSettingsService.governmentFeePostState$.pipe(
      pluck('loading')
    );

  }

  addSubscribers() {

    this.subscribers.resetFormSubscription = this.resetForm$.pipe(
      switchMapTo(this.governmentFee$)
    ).subscribe((governmentFee) => {
      this.resetFormGroup(governmentFee);
    });

    this.subscribers.isCancelDisableSubscription = (this.isCancelButtonDisabled$ as ConnectableObservable<boolean>).connect();

    this.subscribers.submitSubscription = this.submitForm$.pipe(
      filter(() => this.governmentFeesFormGroup.valid),
      switchMap((value) => this.dashboardSettingsService.updateGovernmentFee(value).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status !== 401) {
              this.modalService.showErrorModal(error.error.errors || [error.error]);
            }
            return EMPTY;
          }
        ),
      ))
    ).subscribe();

  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  onSubmit() {
    this.submitForm$.next(this.governmentFeesFormGroup.value);
  }

  resetForm() {
    this.resetForm$.next(null);
  }

  private resetFormGroup(data: GovernmentFee) {
    this.governmentFeesFormGroup.reset(data);
  }

  private createGovernmentFeesFormGroup() {
    this.governmentFeesFormGroup = new FormGroup({
      biometricServiceFee: new FormControl(null),
      i129f: new FormControl(null),
      i130: new FormControl(null),
      i360: new FormControl(null),
      i485: new FormControl(null),
      i485_14: new FormControl(null),
      i600_600a: new FormControl(null),
      i601: new FormControl(null),
      i601a: new FormControl(null),
      i751: new FormControl(null),
      i765: new FormControl(null),
      n400: new FormControl(null),
      n600_n600k: new FormControl(null),
    });
  }
}
