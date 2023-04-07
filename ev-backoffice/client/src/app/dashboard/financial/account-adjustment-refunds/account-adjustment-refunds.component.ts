import { Component, OnInit } from '@angular/core';
import { FormControl, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, switchMap } from 'rxjs/operators';

import { ModalService, PaymentService, UserService } from '../../../core/services';


@Component({
  selector: 'app-account-adjustment-refunds',
  templateUrl: './account-adjustment-refunds.component.html',
})
@DestroySubscribers()
export class AccountAdjustmentRefundsComponent implements OnInit, AddSubscribers {
  representativeIdFormControl: FormControl;
  getRepresentativeIdSubject$: Subject<any> = new Subject();
  representativeBalance$: Observable<number>;

  private subscribers: any = {};

  constructor(
    private userService: UserService,
    private modalService: ModalService,
    private paymentService: PaymentService,
  ) {
    this.representativeIdFormControl = new FormControl(null, {
      validators: [
        Validators.required,
        Validators.pattern('(^[A-Z]\\d{10}$)'),
      ],
      updateOn: 'blur',
    });
  }

  ngOnInit() {
    this.representativeBalance$ = this.paymentService.representativeBalance$;
  }

  addSubscribers() {
    this.subscribers.getRepresentativeIdSubscription = this.getRepresentativeIdSubject$.pipe(
      filter(() => this.representativeIdFormControl.valid),
      switchMap(() => this.userService.getUserIdByEVId(this.representativeIdFormControl.value).pipe(
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

  getRepresentativeId() {
    this.getRepresentativeIdSubject$.next(this.representativeIdFormControl.value);
  }
}
