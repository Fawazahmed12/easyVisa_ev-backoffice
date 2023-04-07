import { Component, OnDestroy, OnInit } from '@angular/core';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { filter, map, skip, withLatestFrom } from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';

import { OrganizationService, PaymentService, UserService } from '../../../core/services';
import { RequestState } from '../../../core/ngrx/utils';
import { EstimatedTax } from '../../../core/models/estimated-tax.model';

@Component({
  selector: 'app-balance-credit',
  templateUrl: './balance-credit.component.html',
})
@DestroySubscribers()
export class BalanceCreditComponent implements OnInit, AddSubscribers, OnDestroy {
  balance$: Observable<number>;
  balanceWithEstTaxes$: Observable<EstimatedTax>;
  currentUserId$: Observable<string>;
  currentRepresentativeUserId$: Observable<number>;
  isAdmin$: Observable<boolean>;
  payNowSubject$: Subject<boolean> = new Subject();
  payBalancePostRequest$: Observable<RequestState<any>>;

  private subscribers: any = {};

  constructor(
    private paymentService: PaymentService,
    private userService: UserService,
    private organizationService: OrganizationService,
  ) {

  }

  ngOnInit() {
    this.payBalancePostRequest$ = this.paymentService.payBalancePostRequest$;
    this.currentRepresentativeUserId$ = this.organizationService.currentRepresentativeUserId$;
    this.balance$ = this.paymentService.balance$;
    this.balanceWithEstTaxes$ = this.paymentService.balanceWithEstTaxes$;
    this.isAdmin$ = this.organizationService.isAdmin$;
    this.currentUserId$ = this.userService.currentUser$.pipe(
      filter((currentUser) => !!currentUser),
      map((currentUser) => currentUser.id)
    );
  }

  addSubscribers() {
    this.subscribers.payNowSubscription = this.payNowSubject$.pipe(
      withLatestFrom(
        this.currentRepresentativeUserId$,
        this.balance$,
      ),
    ).subscribe( ([, id, balance]) => this.paymentService.payBalance({id, balance}));

    this.subscribers.currentRepresentativeUserIdSubscription = this.currentRepresentativeUserId$.pipe(
      skip(1),
      withLatestFrom(this.organizationService.isAdmin$),
      filter(([userId, isAdmin]) => !!userId && isAdmin),
    ).subscribe(([userId, ]) => this.paymentService.getMyBalance(userId));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  payNowClick() {
    this.payNowSubject$.next();
  }
}
