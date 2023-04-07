import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, map, shareReplay, startWith, switchMap, withLatestFrom } from 'rxjs/operators';

import { ModalService, PaymentService, UserService } from '../../../core/services';


@Component({
  selector: 'app-create-credit',
  templateUrl: './create-credit.component.html',
})
@DestroySubscribers()
export class CreateCreditComponent implements OnInit, AddSubscribers {
  representativeBalance$: Observable<number>;
  findUserId$: Observable<number>;
  newBalance$: Observable<number>;
  formSubmitSubject$: Subject<boolean> = new Subject();
  formGroup: FormGroup;


  private subscribers: any = {};

  get descriptionFormControl() {
    return this.formGroup.get('memo');
  }

  get amountFormControl() {
    return this.formGroup.get('amount');
  }

  get amountFormValue$() {
    return this.amountFormControl.valueChanges.pipe(
      startWith(this.amountFormControl.value),
      shareReplay(1)
    );
  }

  constructor(
    private userService: UserService,
    private modalService: ModalService,
    private paymentService: PaymentService,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.representativeBalance$ = this.paymentService.representativeBalance$;
    this.findUserId$ = this.userService.findUserId$;

    this.newBalance$ = this.representativeBalance$.pipe(
      filter((representativeBalance) => !!representativeBalance || representativeBalance === 0),
      switchMap((representativeBalance) => this.amountFormValue$.pipe(
        map((formControlValue) => [representativeBalance, formControlValue])
      )),
      map(([representativeBalance, formControlValue]) => -representativeBalance + formControlValue),
      shareReplay(1)
    );
  }

  addSubscribers() {
    this.subscribers.getRepresentativeIdSubscription = this.formSubmitSubject$.pipe(
      filter(() => this.formGroup.valid),
      withLatestFrom(this.findUserId$),
      switchMap(([, id]) => this.paymentService.postAccountTransaction({
        id,
        accountTransaction: {
          ...this.formGroup.value,
          amount: -this.amountFormControl.value
        }
      }).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status !== 401) {
              this.modalService.showErrorModal(error.error.errors || [error.error]);
            }
            return EMPTY;
          }
        ),
      ))
    ).subscribe(() => this.formGroup.reset());

    this.subscribers.resetFormSubscription = this.findUserId$
    .subscribe(() => this.formGroup.reset());
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      memo: new FormControl(null, Validators.required),
      amount: new FormControl(null, Validators.required),
    });
  }

  formSubmit() {
    this.formSubmitSubject$.next();
  }
}
