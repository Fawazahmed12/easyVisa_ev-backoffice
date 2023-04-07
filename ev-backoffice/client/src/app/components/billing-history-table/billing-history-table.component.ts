import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { EMPTY, Observable } from 'rxjs';
import { catchError, filter, map, pluck, switchMap, take, withLatestFrom } from 'rxjs/operators';

import { TableDataFormat } from '../../task-queue/models/table-data-format.model';
import { AccountTransaction } from '../../core/models/accountTransaction.model';
import { ModalService, PaymentService, UserService } from '../../core/services';
import { OkButtonLg } from '../../core/modals/confirm-modal/confirm-modal.component';
import { RequestState } from '../../core/ngrx/utils';

import { TableHeader } from '../table/models/table-header.model';
import { PaginationService } from "../../core/services";

export interface MyBillingHistoryTableData {
  id: number;
  date: TableDataFormat;
  memo: TableDataFormat;
  amount: TableDataFormat;
}

@Component({
  selector: 'app-billing-history-table',
  templateUrl: './billing-history-table.component.html',
})

@DestroySubscribers()
export class BillingHistoryTableComponent implements OnInit, AddSubscribers, OnDestroy, AddSubscribers {
  @Input() inMyAccount = false;
  @Input() showBalance = false;
  @ViewChild('creditBalance', { static: true }) creditBalance;

  accountTransactions$: Observable<AccountTransaction[]>;
  balance$: Observable<number>;
  accountTransactionsTotal$: Observable<number>;
  billingHistoryTableData$: Observable<MyBillingHistoryTableData[]>;
  accountTransactionsGetRequest$: Observable<RequestState<AccountTransaction[]>>;

  headers: TableHeader[];

  formGroup: FormGroup;

  private subscribers: any = {};

  get headerStyle() {
    return this.inMyAccount ? 'text-white bg-primary' : '';
  }

  get bodyWidth() {
    return this.inMyAccount ? 'col p-0' : 'col-11';
  }

  get maxFormControl() {
    return this.formGroup.get('max');
  }

  get offsetFormControl() {
    return this.formGroup.get('offset');
  }

  get page() {
    return (this.offsetFormControl.value / this.maxFormControl.value) + 1;
  }


  constructor(
    private datePipe: DatePipe,
    private currency: CurrencyPipe,
    private router: Router,
    private paymentService: PaymentService,
    private modalService: ModalService,
    private activatedRoute: ActivatedRoute,
    private userService: UserService,
    private paginationService: PaginationService
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.accountTransactionsGetRequest$ = this.paymentService.accountTransactionsGetRequest$;
    this.accountTransactions$ = this.paymentService.accountTransactions$;
    this.balance$ = this.paymentService.balance$;
    this.accountTransactionsTotal$ = this.paymentService.accountTransactionsTotal$;
    this.headers = [
      {
        title: 'TEMPLATE.BILLING_HISTORY.DATE',
        colName: 'date',
        colClass: 'text-right width-10 p-0 pr-2 border-0',
        bgDarkBlueHeader: true,
      },
      {
        title: 'TEMPLATE.BILLING_HISTORY.DESCRIPTION',
        colName: 'memo',
        colClass: 'text-start pl-4 p-0 border-0',
        bgDarkBlueHeader: true,
      },
      {
        title: 'TEMPLATE.BILLING_HISTORY.AMOUNT',
        colName: 'amount',
        colClass: 'text-right width-15 p-0 border-0',
        bgDarkBlueHeader: true,
      }
    ];

    this.billingHistoryTableData$ = this.accountTransactions$.pipe(
      filter((res) => !!res),
      map((accountTransactions) => accountTransactions.map((accountTransaction) => ({
        id: accountTransaction.id,
        date: {
          data: this.datePipe.transform(new Date(accountTransaction.date), 'MM/dd/yyyy'),
          class: this.transformAmountStyle(accountTransaction.amount)
        },
        memo: {
          data: accountTransaction.memo,
          class: this.transformAmountStyle(accountTransaction.amount)
        },
        amount: {
          data: this.transformAmount(accountTransaction.amount),
          class: `${this.transformAmountStyle(accountTransaction.amount)} pr-3 mr-3`
        }
      }))));
  }

  addSubscribers() {
    this.subscribers.historyNavigationSubscription$ = this.paginationService.getHistoryNavigationSubscription(this.offsetFormControl)

    this.subscribers.queryParamsSubscription = this.activatedRoute.queryParams.pipe(
      filter((params) => !!params),
      map((params) => {
        if (params.max || params.offset) {
          return {
            ...params,
            offset: params.offset,
            max: params.max,
          };
        }
        return params;
      }),
      take(1),
    ).subscribe((res) => this.formGroup.patchValue(res, { emitEvent: false }));

    this.subscribers.getReviewsFormGroupSubscription = this.formGroup.valueChanges.pipe(
      withLatestFrom(
        this.userService.currentUser$.pipe(
          filter((user) => !!user),
          pluck('id')
        ),
        this.userService.findUserId$,
      ),
      switchMap(([, id, foundedUserId]) => this.paymentService.getAccountTransactions({
            id: this.inMyAccount ? id : foundedUserId,
            params: this.formGroup.getRawValue(),
          }
        ).pipe(
          catchError(() => EMPTY),
          take(1),
        )
      ),
    ).subscribe(() => this.addQueryParamsToUrl(this.formGroup.getRawValue()));
  }

  addQueryParamsToUrl(params?) {
    this.router.navigate(['./'], { relativeTo: this.activatedRoute, queryParams: { ...params } });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup(data = {
    offset: 0,
    max: 25,
  }) {
    this.formGroup = new FormGroup({
      offset: new FormControl(data.offset),
      max: new FormControl(data.max),
    });
  }

  pageChange(page) {
    const offset = (page - 1) * this.maxFormControl.value;
    if (offset !== (this.offsetFormControl.value)) {
      this.offsetFormControl.patchValue(offset);
    }
  }

  transformAmount(value) {
    if ((parseInt(value, 10) < 0)) {
      return `($${this.currency.transform(Math.abs(value), '', '', '1.2')})`;
    } else {
      return `$${this.currency.transform(Math.abs(value), '', '', '1.2')}`;
    }
  }

  transformAmountStyle(value) {
    if ((parseInt(value, 10) < 0)) {
      return `text-danger`;
    }
  }

  openModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.ACCOUNT.PAYMENT.CREDIT_BALANCE',
      body: this.creditBalance,
      buttons: [OkButtonLg],
      centered: true,
      size: 'lg',
    }).pipe(
      catchError(() => EMPTY),
    );
  }
}
