import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Router } from '@angular/router';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Observable, Subject } from 'rxjs';
import { filter, map, pluck, switchMap } from 'rxjs/operators';
import { fromPromise } from 'rxjs/internal-compatibility';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ConfigDataService, TaxesService, UserService } from '../../../../../../../core/services';
import { EstimatedTax } from '../../../../../../../core/models/estimated-tax.model';
import { TaxTypes } from '../../../../../../../core/models/tax-types.enum';


@Component({
  selector: 'app-cancel-membership',
  templateUrl: './membership-modal.component.html',
  styleUrls: ['./membership-modal.component.scss'],
})
@DestroySubscribers()
export class MembershipModalComponent implements OnInit, OnDestroy, AddSubscribers {
  reActivationFee$: Observable<number>;
  activeMembership$: Observable<boolean>;
  title$: Observable<string>;
  reactivationFeeWithTax$: Observable<EstimatedTax>;

  clickToRouteSubject$: Subject<boolean> = new Subject<boolean>();

  confirmFormControl = new FormControl(false);

  private subscribers: any = {};

  constructor(
    private activeModal: NgbActiveModal,
    private configDataService: ConfigDataService,
    private userService: UserService,
    private taxesService: TaxesService,
    private router: Router,
  ) {
  }

  ngOnInit() {
    this.reactivationFeeWithTax$ = this.taxesService.reactivationFeeWithTax$;
    this.reActivationFee$ = this.configDataService.feeDetails$.pipe(
      filter((feeDetails) => !!feeDetails),
      pluck('membershipReactivationFee'),
    );

    this.activeMembership$ = this.userService.activeMembership$;
    this.title$ = this.activeMembership$.pipe(
      map((activeMembership) => activeMembership ?
        'TEMPLATE.ACCOUNT.PROFILE.EASY_VISA_MEMBERSHIP_STATUS.MODALS.CANCEL_MEMBERSHIP.HEADER'
        : 'TEMPLATE.ACCOUNT.PROFILE.EASY_VISA_MEMBERSHIP_STATUS.MODALS.REACTIVATE_MEMBERSHIP.HEADER')
    );
  }

  addSubscribers() {
    this.subscribers.clickToRouteSubjectSubscription = this.clickToRouteSubject$.pipe(
      switchMap(() => this.activeMembership$),
      switchMap((activeMembership) => {
        if (activeMembership) {
          return fromPromise(this.router.navigate(['task-queue', 'clients']));
        } else {
          return fromPromise(this.router.navigate(['account', 'payment-fee-schedule']));
        }
      })
    ).subscribe((res) => this.closeModal());

    this.subscribers.activeMembershipSubscription = this.activeMembership$.pipe(
      filter(activeMembership => !activeMembership)
    ).subscribe(() => this.taxesService.postEstimatedTax({type: TaxTypes.MEMBERSHIP_REACTIVATION_FEE}));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  clickToRoute() {
    this.clickToRouteSubject$.next(true);
  }

  closeModal() {
    this.activeModal.dismiss();
  }

  confirmModal() {
    this.activeModal.close(this.confirmFormControl.value);
  }
}
