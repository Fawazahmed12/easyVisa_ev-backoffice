import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl } from '@angular/forms';

import { filter, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { OrganizationService, UserService } from '../../core/services';
import { Organization } from '../../core/models/organization.model';
import { User } from '../../core/models/user.model';

@Component({
  selector: 'app-payment-fee-schedule',
  templateUrl: './payment-fee-schedule.component.html',
})
@DestroySubscribers()
export class PaymentFeeScheduleComponent implements OnInit, OnDestroy, AddSubscribers {
  isMe$: Observable<boolean>;
  currentRepresentativeId$: Observable<number>;
  currentUser$: Observable<User>;
  isAdmin$: Observable<boolean>;
  representativeIdFormControl: FormControl = this.organizationService.representativeIdControl;

  private activeOrganization$: Observable<Organization>;
  private subscribers: any = {};

  constructor(
    private organizationService: OrganizationService,
    private userService: UserService,
  ) {

  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.isAdmin$ = this.organizationService.isAdmin$;
    this.currentUser$ = this.userService.currentUser$;
    this.isMe$ = this.userService.isCurrentRepresentativeMe$;
  }

  addSubscribers() {
    this.subscribers.activeOrganizationSubscription = this.activeOrganization$.pipe(
      filter((activeOrganization) => !!activeOrganization),
      withLatestFrom((this.userService.currentUser$))
    ).subscribe(([activeOrganization, currentUser]) => {
      if (activeOrganization.isAdmin) {
        this.representativeIdFormControl = this.organizationService.representativeIdControl;
      } else {
        this.representativeIdFormControl = new FormControl({value: currentUser.profile.id, disabled: true});
      }
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }
}
