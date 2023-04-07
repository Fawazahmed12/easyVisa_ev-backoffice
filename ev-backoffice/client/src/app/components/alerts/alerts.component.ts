import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { DatePipe } from '@angular/common';

import { combineLatest, EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, debounceTime, filter, map, switchMap, take, withLatestFrom } from 'rxjs/operators';

import { Dictionary } from '@ngrx/entity';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ModalService, NotificationsService, OrganizationService } from '../../core/services';
import { RequestState } from '../../core/ngrx/utils';
import { Role } from '../../core/models/role.enum';
import { Alert } from '../../task-queue/models/alert.model';
import { TableDataFormat } from '../../task-queue/models/table-data-format.model';
import { AlertsService } from '../../core/services/alerts.service';
import { TaskQueueCounts } from '../../core/models/task-queue-counts.model';

import { TableHeader } from '../table/models/table-header.model';
import { PaginationService } from "../../core/services";

export interface AlertsTableData {
  id: number;
  checkbox: FormControl;
  star: TableDataFormat;
  subject: TableDataFormat;
  source: TableDataFormat;
  date: TableDataFormat;
  read: TableDataFormat;
  viewed?: boolean;
  active?: boolean;
}

@Component({
  selector: 'app-alerts',
  templateUrl: './alerts.component.html',
  styleUrls: ['./alerts.component.scss']
})
@DestroySubscribers()
export class AlertsComponent implements OnInit, OnDestroy, AddSubscribers {
  activeAlert$: Observable<Alert>;
  activeAlertId$: Observable<number>;
  alerts$: Observable<Alert[]>;
  alertsEntities$: Observable<Dictionary<Alert>>;
  tableData$: Observable<AlertsTableData[]>;
  totalAlerts$: Observable<string>;
  getAlertPutRequest$: Observable<RequestState<Alert>>;
  getAlertsDeleteRequest$: Observable<RequestState<number[]>>;
  starAlertSubject$: Subject<number> = new Subject();
  removeAlertsSubject$: Subject<number[]> = new Subject();
  activeAlertSubject$: Subject<number> = new Subject();
  taskQueueNotifications$: Observable<TaskQueueCounts>;

  tableHeader: TableHeader[];
  formGroup: FormGroup;
  selectedAlertsIds: FormControl = new FormControl([]);

  ROLE_OWNER = Role.ROLE_OWNER;
  ROLE_EV = Role.ROLE_EV;

  private subscribers: any = {};

  get sortFormControl() {
    return this.formGroup.get('sort');
  }

  get orderFormControl() {
    return this.formGroup.get('order');
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
    private alertsService: AlertsService,
    private activatedRoute: ActivatedRoute,
    private notificationsService: NotificationsService,
    private organizationService: OrganizationService,
    private router: Router,
    private modalService: ModalService,
    private datePipe: DatePipe,
    private paginationService: PaginationService
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.activeAlertId$ = this.alertsService.activeAlertId$;
    this.activeAlert$ = this.alertsService.activeAlert$;
    this.totalAlerts$ = this.alertsService.totalAlerts$;
    this.alerts$ = this.alertsService.alerts$;
    this.alertsEntities$ = this.alertsService.alertsEntities$;
    this.getAlertPutRequest$ = this.alertsService.getAlertPutRequest$;
    this.getAlertsDeleteRequest$ = this.alertsService.getAlertsDeleteRequest$;
    this.taskQueueNotifications$ = this.notificationsService.taskQueueNotifications$;
    this.tableHeader = [
      { title: '', colName: 'checkbox', colClass: 'text-center width-5' },
      { title: '', colName: 'star', sortBy: true, action: true, colClass: 'text-center width-5' },
      { title: 'FORM.ALERTS.SUBJECT', colName: 'subject' },
      { title: 'FORM.ALERTS.SOURCE', colName: 'source', sortBy: true, colClass: 'text-center width-15' },
      { title: 'FORM.ALERTS.DATE', colName: 'date', sortBy: true, colClass: 'text-center width-15' },
      { title: 'FORM.ALERTS.READ', colName: 'read', sortBy: true, colClass: 'text-center width-10' },
    ];

    this.tableData$ = combineLatest([
      this.alerts$,
      this.activeAlertId$,
    ]).pipe(
      map(([alerts, activeAlertId]) => alerts.map((alert) => ({
        id: alert.id,
        checkbox: new FormControl(false),
        star: { data: '', class: alert.starred ? 'fa fa-star text-warning' : 'fa fa-star-o' },
        subject: { data: alert.subject },
        source: { data: alert.source },
        date: { data: this.datePipe.transform(new Date(alert.createdOn), 'MM/dd/yyyy h:mm aaa') },
        read: { data: '', class: alert.read ? 'fa fa-check' : '' },
        viewed: alert.read,
        active: activeAlertId && alert.id === activeAlertId,
      })))
    );
  }

  addSubscribers() {
    this.subscribers.historyNavigationSubscription$ = this.paginationService.getHistoryNavigationSubscription(this.offsetFormControl)

    this.subscribers.starSubscription = this.starAlertSubject$.pipe(
      withLatestFrom(this.alertsEntities$)
    ).pipe(
      switchMap(([id, entities]) => this.alertsService.updateAlert({ id, starred: !entities[ id ].starred }).pipe(
          catchError((error: HttpErrorResponse) => {
            if (error.status !== 401) {
              this.modalService.showErrorModal(error.error.errors || [error.error]);
            }
            return EMPTY;
          }),
        )
      )
    ).subscribe();

    this.subscribers.removeAlertsSubscription = this.removeAlertsSubject$.pipe(
      switchMap((ids: number[]) => this.alertsService.removeAlerts({ ids, query: { ...this.formGroup.value } }).pipe(
        catchError((error: HttpErrorResponse) => {
          if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [error.error]);
          }
          return EMPTY;
        }),
      ))
    ).subscribe(() => this.selectedAlertsIds.patchValue([]));

    this.subscribers.queryParamsSubscription = this.activatedRoute.queryParams.pipe(
      filter((params) => !!params),
      take(1),
    ).subscribe((res) => this.formGroup.patchValue(res, { emitEvent: false }));

    this.subscribers.getAlertsFormGroupSubscription = this.formGroup.valueChanges.pipe(
      switchMap(() => this.alertsService.getAlerts(this.formGroup.value).pipe(
        catchError(() => EMPTY),
        take(1),
      )),
    ).subscribe(() => this.addQueryParamsToUrl(this.formGroup.value));

    this.subscribers.activeAlertSubscription = this.activeAlertSubject$.pipe(
      debounceTime(500),
      withLatestFrom(this.activeAlertId$),
      filter(([id, activeAlertId]) => id !== activeAlertId),
      switchMap(([id]) => this.alertsEntities$.pipe(
        take(1),
        switchMap((entities) => {
          if (!entities[ id ].read) {
            return this.alertsService.updateAlert({ id, read: true })
              .pipe(
                take(1),
                map((res: Alert) => {
                  this.notificationsService.decreaseAlertsNotifications();
                  return id;
                }),
                catchError((error: HttpErrorResponse) => {
                  if (error.status !== 401) {
                    this.modalService.showErrorModal(error.error.errors || [error.error]);
                  }
                  return of(id);
                }),
              );
          }
          return of(id);
        })
      )),
    ).subscribe((id) => this.alertsService.setActiveAlert(id));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  addQueryParamsToUrl(params?) {
    this.router.navigate(['./'], { relativeTo: this.activatedRoute, queryParams: { ...params } });
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      sort: new FormControl('date'),
      order: new FormControl('desc'),
      offset: new FormControl(0),
      max: new FormControl(25),
    });
  }

  pageChange(page) {
    const offset = (page - 1) * this.maxFormControl.value;
    if (offset !== (this.offsetFormControl.value)) {
      this.offsetFormControl.patchValue(offset);
    }
  }

  starAlert(alert) {
    this.starAlertSubject$.next(alert.id);
  }

  viewAlert(alert) {
    this.activeAlertSubject$.next(alert.id);
  }

  sortBy(colName) {
    if (colName !== this.sortFormControl.value) {
      this.formGroup.patchValue({ sort: colName });
    } else {
      this.formGroup.patchValue({ order: this.orderFormControl.value === 'asc' ? 'desc' : 'asc' });
    }
  }

  removeAlerts() {
    if (this.selectedAlertsIds.value.length) {
      this.removeAlertsSubject$.next(this.selectedAlertsIds.value);
    }
  }
}
