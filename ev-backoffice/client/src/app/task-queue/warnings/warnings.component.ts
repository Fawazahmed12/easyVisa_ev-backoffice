import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, debounceTime, filter, map, skip, switchMap, take, withLatestFrom } from 'rxjs/operators';
import { combineLatest, EMPTY, Observable, of, Subject } from 'rxjs';

import { Dictionary } from '@ngrx/entity';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { ModalService, NotificationsService, OrganizationService } from '../../core/services';
import { Organization } from '../../core/models/organization.model';
import { RequestState } from '../../core/ngrx/utils';
import { TableHeader } from '../../components/table/models/table-header.model';
import { getRepresentativeLabel } from '../../shared/utils/get-representative-label';
import { TaskQueueCounts } from '../../core/models/task-queue-counts.model';

import { TableDataFormat } from '../models/table-data-format.model';
import { Warning } from '../models/warning.model';

import { WarningsService } from './warnings.service';
import { PaginationService } from "../../core/services";

export interface WarningsTableData {
  id: number;
  checkbox: FormControl;
  star: TableDataFormat;
  client?: TableDataFormat;
  representative: TableDataFormat;
  subject: TableDataFormat;
  date: TableDataFormat;
  read: TableDataFormat;
  viewed?: boolean;
  active?: boolean;
}

@Component({
  selector: 'app-warnings',
  templateUrl: './warnings.component.html',
  styleUrls: ['./warnings.component.scss'],
})
@DestroySubscribers()
export class WarningsComponent implements OnInit, OnDestroy, AddSubscribers {
  activeWarning$: Observable<Warning>;
  activeWarningId$: Observable<number>;
  activeOrganization$: Observable<Organization>;
  getWarningsDeleteRequest$: Observable<RequestState<number[]>>;
  taskQueueNotifications$: Observable<TaskQueueCounts>;
  warnings$: Observable<Warning[]>;
  totalWarnings$: Observable<string>;
  warningsEntities$: Observable<Dictionary<Warning>>;
  warningsTableHeader$: Observable<TableHeader[]>;
  warningsTableData$: Observable<WarningsTableData[]>;
  activeWarningSubject$: Subject<number> = new Subject();
  removeWarningsSubject$: Subject<number[]> = new Subject();
  starWarningSubject$: Subject<number> = new Subject();
  getWarningsGetRequest$: Observable<RequestState<Warning[]>>;

  formGroup: FormGroup;
  selectedWarningsIds: FormControl = new FormControl([]);

  private subscribers: any = {};

  get representativeIdFormControl() {
    return this.formGroup.get('representativeId');
  }

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
    private activatedRoute: ActivatedRoute,
    private datePipe: DatePipe,
    private notificationsService: NotificationsService,
    private modalService: ModalService,
    private organizationService: OrganizationService,
    private router: Router,
    private warningsService: WarningsService,
    private paginationService: PaginationService
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.getWarningsGetRequest$ = this.warningsService.getWarningsGetRequest$;
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.activeWarningId$ = this.warningsService.activeWarningId$;
    this.activeWarning$ = this.warningsService.activeWarning$;
    this.totalWarnings$ = this.warningsService.totalWarnings$;
    this.getWarningsDeleteRequest$ = this.warningsService.getWarningsDeleteRequest$;
    this.taskQueueNotifications$ = this.notificationsService.taskQueueNotifications$;
    this.warnings$ = this.warningsService.warnings$;
    this.warningsEntities$ = this.warningsService.warningsEntities$;
    this.warningsTableHeader$ = this.activeOrganization$.pipe(
      filter(organization => !!organization),
      map((organization) => {
        const representativeType = getRepresentativeLabel(organization.organizationType);
        return [
          { title: '', colName: 'checkbox', colClass: 'text-center width-5' },
          { title: '', colName: 'star', sortBy: true, action: true, colClass: 'text-center width-5' },
          { title: 'FORM.ALERTS.CLIENT', colName: 'applicant', sortBy: true, colClass: 'width-30' },
          { title: representativeType, sortBy: true, colName: 'representative', colClass: 'width-15' },
          { title: 'FORM.ALERTS.SUBJECT', colName: 'subject', sortBy: true, colClass: 'width-20' },
          { title: 'FORM.ALERTS.DATE', colName: 'date', sortBy: true, colClass: 'text-center width-15' },
          { title: 'FORM.ALERTS.READ', colName: 'read', sortBy: true, colClass: 'text-center width-10' },
        ];
      })
    );

    this.warningsTableData$ = combineLatest([
      this.warnings$,
      this.activeWarningId$,
    ]).pipe(
      map(([warnings, activeWarningId]) => warnings.map((warning) => ({
        id: warning.id,
        checkbox: new FormControl(false),
        star: { data: '', class: warning.starred ? 'fa fa-star text-warning' : 'fa fa-star-o' },
        applicant: { data: warning.clientName },
        representative: { data: warning.representativeName },
        subject: { data: warning.subject },
        date: { data: this.datePipe.transform(new Date(warning.createdOn), 'MM/dd/yyyy h:mm aaa') },
        read: { data: '', class: warning.read ? 'fa fa-check' : '' },
        viewed: warning.read,
        active: activeWarningId && warning.id === activeWarningId,
      })))
    );
  }

  addSubscribers() {
    this.subscribers.historyNavigationSubscription$ = this.paginationService.getHistoryNavigationSubscription(this.offsetFormControl)

    this.subscribers.queryParamsWarningSubscription = this.activatedRoute.queryParams.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
      take(1),
    ).subscribe(([params, [repId, orgId]]) => this.formGroup.patchValue({
      ...params,
      organizationId: parseInt(params?.organizationId, 10) || orgId,
      representativeId: parseInt(params?.representativeId, 10) || repId,
    }, { emitEvent: false }));

    this.subscribers.currentRepIdWarningSubscription = this.organizationService.currentRepIdOrgId$.pipe(
      skip(1),
    ).subscribe(([representativeId, organizationId]) => this.formGroup.patchValue({ representativeId, organizationId }));


    this.subscribers.getWarningsFormGroupSubscription = this.formGroup.valueChanges.pipe(
      switchMap(() => this.warningsService.getWarnings(this.formGroup.getRawValue()).pipe(
        catchError(() => {
          this.router.navigate([]);
          return EMPTY;
        }),
        take(1),
      )),
    ).subscribe();

    this.subscribers.articlesFormGroupSubscription = this.formGroup.valueChanges
      .subscribe(() => this.addQueryParamsToUrl(this.formGroup.getRawValue()));

    this.subscribers.activeWarningSubscription = this.activeWarningSubject$.pipe(
      debounceTime(500),
      withLatestFrom(this.activeWarningId$),
      filter(([id, activeWarningId]) => id !== activeWarningId),
      switchMap(([id]) => this.warningsEntities$.pipe(
        take(1),
        switchMap((entities) => {
          if (!entities[ id ].read) {
            return this.warningsService.updateWarning({ id, read: true })
              .pipe(
                take(1),
                map((res: Warning) => {
                  this.notificationsService.decreaseWarningsNotifications();
                  return res.id;
                }),
                catchError((error: HttpErrorResponse) => {
                  if (error.status !== 401) {
                    this.modalService.showErrorModal(error.error.errors || [error.error]);
                  }
                  return of(id);
                }),
              );
          } else {
            return of(id);
          }
        })
      )),
    ).subscribe((id) => this.warningsService.setActiveWarning(id));

    this.subscribers.removeWarningsSubscription = this.removeWarningsSubject$.pipe(
      switchMap((ids: number[]) => this.warningsService.removeWarnings(
        { ids, query: { ...this.formGroup.getRawValue() } }
      ).pipe(
        catchError(() => EMPTY),
      ))
    ).subscribe(() => this.selectedWarningsIds.patchValue([]));

    this.subscribers.starWarningSubscription = this.starWarningSubject$.pipe(
      withLatestFrom(this.warningsEntities$)
    ).subscribe(([id, entities]) => this.warningsService.updateWarning({ id, starred: !entities[ id ].starred }));
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      sort: new FormControl('date'),
      order: new FormControl('desc'),
      representativeId: new FormControl(),
      organizationId: new FormControl(),
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

  addQueryParamsToUrl(params?) {
    this.router.navigate(['./'], { relativeTo: this.activatedRoute, queryParams: { ...params } });
  }

  sortBy(colName) {
    if (colName !== this.sortFormControl.value) {
      this.formGroup.patchValue({ sort: colName });
    } else {
      this.formGroup.patchValue({ order: this.orderFormControl.value === 'asc' ? 'desc' : 'asc' });
    }
  }

  starWarning(warning) {
    this.starWarningSubject$.next(warning.id);
  }

  viewWarning(warning) {
    this.activeWarningSubject$.next(warning.id);
  }

  removeWarnings() {
    if (this.selectedWarningsIds.value.length) {
      this.removeWarningsSubject$.next(this.selectedWarningsIds.value);
    }
  }
}
