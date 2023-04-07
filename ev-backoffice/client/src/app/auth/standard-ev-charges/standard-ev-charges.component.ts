import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { FormControl, Validators } from '@angular/forms';

import { combineLatest, Observable, Subject } from 'rxjs';
import { filter, map, switchMap, switchMapTo } from 'rxjs/operators';

import { isEqual, sortBy } from 'lodash-es';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { FeeDetails } from '../../core/models/fee-details.model';
import { AttorneyType } from '../../core/models/attorney-type.enum';
import { ConfigDataService, FeeScheduleService, ModalService, UserService } from '../../core/services';
import { RegistrationStatus } from '../../core/models/registration-status.enum';
import { SignUpService } from '../services';
import { FeeSchedule } from '../../core/models/fee-schedule.model';
import { ConfirmButtonType } from '../../core/modals/confirm-modal/confirm-modal.component';

export interface RecurringCharge {
  title: string;
  description: string;
  price?: string | number;
  note?: boolean;
  doubleNote?: boolean;
  priceRange?: boolean;
}

@Component({
  selector: 'app-standard-ev-charges',
  templateUrl: './standard-ev-charges.component.html',
  styleUrls: ['./standard-ev-charges.component.scss'],
})
@DestroySubscribers()
export class StandardEvChargesComponent implements OnInit {

  @ViewChild('perApplicantChargesModal', { static: true }) perApplicantChargesModal;

  feeDetails$: Observable<FeeDetails> = new Observable<FeeDetails>();
  recurringCharges$: Observable<RecurringCharge[]>;
  isEqualReferral$: Observable<boolean>;
  redirectSubject$: Subject<void> = new Subject<void>();
  goToRepInfoSubject$: Subject<void> = new Subject<void>();
  referralSubject$: Subject<void> = new Subject<void>();
  referralFormControl: FormControl = new FormControl('', [
    Validators.required,
    Validators.email,
  ]);
  minApplicantFee = 0;
  maxApplicantFee = 0;
  cloudStorageFee = 0;
  feeSchedules: FeeSchedule[];

  private buttons = [
    {
      label: 'FORM.BUTTON.OK',
      type: ConfirmButtonType.Dismiss,
      className: 'btn btn-primary mr-2 min-w-100',
    },
  ];

  private subscribers: any = {};

  constructor(
    private router: Router,
    private configDataService: ConfigDataService,
    private signUpService: SignUpService,
    private userService: UserService,
    private feeScheduleService: FeeScheduleService,
    private modalService: ModalService
  ) {
  }

  ngOnInit() {
    this.feeDetails$ = this.configDataService.feeDetails$;
    this.recurringCharges$ = this.feeDetails$.pipe(
      filter((feeDetails) => !!feeDetails),
      map((fees) =>
        ([
          {
            title: 'TEMPLATE.AUTH.STANDARD_CHARGES.PER_APPLICANT_FEE',
            description: 'TEMPLATE.AUTH.STANDARD_CHARGES.PER_APPLICANT_FEE_DESCRIPTION',
            priceRange: true,
            note: true,
          },
          {
            title: 'TEMPLATE.AUTH.STANDARD_CHARGES.MAINTENANCE_FEE',
            description: 'TEMPLATE.AUTH.STANDARD_CHARGES.MAINTENANCE_FEE_DESCRIPTION',
            price: fees.maintenanceFee,
            note: true,
          },
          {
            title: 'TEMPLATE.AUTH.STANDARD_CHARGES.CLOUD_STORAGE_FEE',
            description: 'TEMPLATE.AUTH.STANDARD_CHARGES.CLOUD_STORAGE_FEE_DESCRIPTION',
            price: this.cloudStorageFee
          },
        ])
      )
    );

    this.isEqualReferral$ = combineLatest([
      this.signUpService.referralEmail$,
      this.referralFormControl.valueChanges
    ]).pipe(
      map(([referralEmail, formControlValue]) => isEqual(referralEmail, formControlValue))
    );
  }

  addSubscribers() {
    this.subscribers.redirectSubscription = this.redirectSubject$.pipe(
      switchMap(() => this.userService.registrationRepresentativeType$),
    )
      .subscribe((representativeType: string) => {
        if (!representativeType || representativeType === AttorneyType.SOLO_PRACTITIONER) {
          this.router.navigate(['auth', 'select-representative-type']);
        } else if (!representativeType || representativeType === AttorneyType.MEMBER_OF_A_LAW_FIRM) {
          this.router.navigate(['auth', 'representative-message-page']);
        } else {
          this.router.navigate(['account', 'profile']);
        }
      });

    this.subscribers.goToRepInfoSubscription = this.goToRepInfoSubject$.pipe(
      switchMapTo(this.userService.registrationRepresentativeType$)
    ).subscribe((registrationRepresentativeType) => {
      registrationRepresentativeType === RegistrationStatus.CONVERT_TO_ATTORNEY ?
        this.router.navigate(['auth', 'rep-info-payment-method']) : this.router.navigate(['auth', 'rep-basic-info']);
    });

    this.subscribers.addReferralSubscription = this.referralSubject$.pipe(
      filter(() => this.referralFormControl.valid),
    ).subscribe(() => this.signUpService.addReferral(this.referralFormControl.value));

    this.subscribers.feeScheduleSettingsSubscription = this.feeScheduleService.feeScheduleSettings$.pipe(
      filter((feeSchedule) => !!feeSchedule),
    ).subscribe((feeSchedules: FeeSchedule[]) => {
      const filteredFeeSchedules: FeeSchedule[] = feeSchedules.filter((data) => data.amount != 0);
      this.feeSchedules = filteredFeeSchedules;
      const orderedFeeSchedules: FeeSchedule[] = sortBy(filteredFeeSchedules, 'amount');
      this.minApplicantFee = orderedFeeSchedules.length ? orderedFeeSchedules[ 0 ].amount : 0;
      this.maxApplicantFee = orderedFeeSchedules.length ? orderedFeeSchedules[ orderedFeeSchedules.length - 1 ].amount : 0;
    });
  }

  goToRepBasicInfo() {
    this.goToRepInfoSubject$.next();
  }

  goToSelectRepresentativeType() {
    this.redirectSubject$.next();
  }

  applyReferral() {
    this.referralSubject$.next();
  }

  openPerApplicantChargesModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.AUTH.STANDARD_CHARGES.PER_APPLICANT_CHARGES.TITLE',
      body: this.perApplicantChargesModal,
      buttons: this.buttons,
      centered: true,
      size: 'lg',
      windowClass: 'custom-modal-lg per-app-charge-modal-lg'
    });
  }
}
