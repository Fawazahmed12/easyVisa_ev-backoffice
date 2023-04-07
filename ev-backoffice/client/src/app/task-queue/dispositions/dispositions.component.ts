import { Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormControl, FormGroup } from '@angular/forms';
import { DatePipe } from '@angular/common';

import { catchError, debounceTime, filter, map, skip, switchMap, take, withLatestFrom } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { combineLatest, EMPTY, Observable, of, Subject } from 'rxjs';
import { Dictionary } from '@ngrx/entity';
import { fromPromise } from 'rxjs/internal-compatibility';

import { EmailTemplatesService, ModalService, NotificationsService, OrganizationService } from '../../core/services';
import { getRepresentativeLabel } from '../../shared/utils/get-representative-label';
import { TableHeader } from '../../components/table/models/table-header.model';
import { TaskQueueCounts } from '../../core/models/task-queue-counts.model';
import { Organization } from '../../core/models/organization.model';
import { RequestState } from '../../core/ngrx/utils';
import { DocumentFileType, DocumentFileTypeIcons, DocumentImageFileType } from '../../documents/models/documents.model';
import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';
import { OrganizationType } from '../../core/models/organization-type.enum';

import { TableDataFormat } from '../models/table-data-format.model';
import { Disposition } from '../models/dispositions.model';
import { DispositionData } from '../models/disposition-data.model';

import { DispositionsService } from './dispositions.service';
import { RejectFileComponent } from './reject-file/reject-file.component';
import { EnlargeDocumentComponent } from './enlarge-document/enlarge-document.component';
import { PaginationService } from "../../core/services";


export interface DispositionsTableData {
  id: number;
  client: TableDataFormat;
  representative: TableDataFormat;
  document: TableDataFormat;
  benefit: TableDataFormat;
  date: TableDataFormat;
  active?: boolean;
}

@Component({
  selector: 'app-dispositions',
  templateUrl: './dispositions.component.html',
  styleUrls: ['./dispositions.component.scss']
})

@DestroySubscribers()
export class DispositionsComponent implements OnInit, OnDestroy, AddSubscribers {
  activeOrganization$: Observable<Organization>;
  getDispositionsGetRequest$: Observable<RequestState<Disposition[]>>;
  getDispositionDataGetRequest$: Observable<RequestState<DispositionData>>;
  dispositions$: Observable<Disposition[]>;
  totalDispositions$: Observable<string>;
  activeDisposition$: Observable<Disposition>;
  activeDispositionData$: Observable<DispositionData>;
  activeDispositionId$: Observable<number>;
  dispositionsEntities$: Observable<Dictionary<Disposition>>;
  dispositionsTableHeader$: Observable<TableHeader[]>;
  dispositionsTableData$: Observable<DispositionsTableData[]>;
  taskQueueNotifications$: Observable<TaskQueueCounts>;
  activeDispositionSubject$: Subject<number> = new Subject<number>();
  openRejectModalSubject$: Subject<boolean> = new Subject<boolean>();
  getDispositionPutRequest$: Observable<RequestState<Disposition>>;
  approveDispositionSubject$: Subject<boolean> = new Subject<boolean>();

  formGroup: FormGroup;

  imageFileTypes = Object.values(DocumentImageFileType);
  soloPractitioner = OrganizationType.SOLO_PRACTICE;
  EmployeePosition = EmployeePosition;

  private subscribers: any = {};

  get organizationIdFormControl() {
    return this.formGroup.get('organizationId');
  }

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
    private emailTemplatesService: EmailTemplatesService,
    private notificationsService: NotificationsService,
    private organizationService: OrganizationService,
    private dispositionsService: DispositionsService,
    private ngbModal: NgbModal,
    private activatedRoute: ActivatedRoute,
    private modalService: ModalService,
    private datePipe: DatePipe,
    private router: Router,
    private paginationService: PaginationService
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.getDispositionPutRequest$ = this.dispositionsService.getDispositionPutRequest$;
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.getDispositionsGetRequest$ = this.dispositionsService.getDispositionsGetRequest$;
    this.dispositions$ = this.dispositionsService.dispositions$;
    this.activeDisposition$ = this.dispositionsService.activeDisposition$;
    this.activeDispositionData$ = this.dispositionsService.activeDispositionData$;
    this.totalDispositions$ = this.dispositionsService.totalDispositions$;
    this.getDispositionDataGetRequest$ = this.dispositionsService.getDispositionDataGetRequest$;
    this.activeDispositionId$ = this.dispositionsService.activeDispositionId$;
    this.dispositionsEntities$ = this.dispositionsService.dispositionsEntities$;
    this.taskQueueNotifications$ = this.notificationsService.taskQueueNotifications$;
    this.dispositionsTableHeader$ = this.activeOrganization$.pipe(
      filter(organization => !!organization),
      map((organization) => {
        const representativeType = getRepresentativeLabel(organization.organizationType);
        return [
          { title: 'TEMPLATE.TASK_QUEUE.DISPOSITIONS.CLIENT', colName: 'client', sortBy: true, colClass: 'width-25' },
          { title: representativeType, sortBy: true, colName: 'representative', colClass: 'width-10' },
          { title: 'TEMPLATE.TASK_QUEUE.DISPOSITIONS.DOCUMENT', colName: 'document', colClass: 'width-30' },
          { title: 'TEMPLATE.TASK_QUEUE.DISPOSITIONS.BENEFIT', colName: 'benefit', colClass: 'text-center width-15' },
          {
            title: 'TEMPLATE.TASK_QUEUE.DISPOSITIONS.DATE',
            colName: 'date',
            sortBy: true,
            colClass: 'text-center width-20'
          }
        ];
      })
    );

    this.dispositionsTableData$ = combineLatest([
      this.dispositions$,
      this.activeDispositionId$,
    ]).pipe(
      switchMap(([dispositions, activeDispositionId]) => of({ dispositions, activeDispositionId })),
      map(({ dispositions, activeDispositionId }) => dispositions.map((disposition) => ({
        id: disposition.id,
        client: { data: disposition.applicantName },
        document: { data: disposition.fileName },
        representative: { data: disposition.representativeName },
        benefit: { data: disposition.benefitCategory },
        date: { data: this.datePipe.transform(new Date(disposition.createdDate), 'MM/dd/yyyy h:mm aaa') },
        active: activeDispositionId && disposition.id === activeDispositionId
      })))
    )
    ;
  }

  addSubscribers() {
    this.subscribers.historyNavigationSubscription$ = this.paginationService.getHistoryNavigationSubscription(this.offsetFormControl)

    this.subscribers.queryParamsSubscription = this.activatedRoute.queryParams.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
      take(1),
    ).subscribe(([params, [repId, orgId]]) => this.formGroup.patchValue({
      ...params,
      organizationId: parseInt(params?.organizationId, 10) || orgId,
      representativeId: parseInt(params?.representativeId, 10) || repId,
    }, { emitEvent: false }));

    this.subscribers.formGroupForaddQuerySubscription = this.formGroup.valueChanges.pipe(
    ).subscribe(() => {
      this.addQueryParamsToUrl(this.formGroup.getRawValue());
    });

    this.subscribers.currentRepIdDispositionsSubscription = this.organizationService.currentRepIdOrgId$.pipe(
      skip(1),
    ).subscribe(([representativeId, organizationId]) => this.formGroup.patchValue({ representativeId, organizationId }));

    this.subscribers.formGroupForGetDispositionsSubscription = this.formGroup.valueChanges
      .subscribe(() => {
        this.dispositionsService.resetActiveDisposition();
        this.dispositionsService.getDispositions(this.formGroup.getRawValue());
      });

    this.subscribers.activeDispositionSubscription = this.activeDispositionSubject$.pipe(
      debounceTime(500),
      withLatestFrom(this.activeDispositionId$),
      filter(([id, activeDispositionId]) => id !== activeDispositionId),
    ).subscribe(([id]) => {
      this.dispositionsService.setActiveDisposition(id);
    });

    this.subscribers.openRejectModalSubscription = this.openRejectModalSubject$.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
      map(([, [representativeId,]]) => {
        this.dispositionsService.getEmailTemplateForPopUp(representativeId);
        return representativeId;
      }),
      switchMap((representativeId) => this.openRejectDocumentModal().pipe(
        map(data => [data, representativeId])
      )),
    ).subscribe(([data, representativeId]) =>
      this.dispositionsService.rejectDisposition({
        approve: false,
        rejectionMailMessage: data.emailTemplate,
        rejectionMailSubject: data.subject,
        organizationId: data.organizationId,
        representativeId
      })
    );

    this.subscribers.approveDispositionSubscription = this.approveDispositionSubject$.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
    ).subscribe(([, [representativeId, organizationId]]) => {
      this.dispositionsService.approveDisposition({
        approve: true,
        representativeId,
        organizationId,
      });
    });

    this.subscribers.dispositionApproveRejectErrorSubscription = this.dispositionsService.putDispositionFailAction$
      .pipe(filter((action: any) => !!action))
      .subscribe((data) => {
        this.formGroup.patchValue({});
      });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup() {
    this.formGroup = new FormGroup({
      sort: new FormControl('date'),
      order: new FormControl('asc'),
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

  openRejectDocumentModal() {
    const modalRef = this.ngbModal.open(RejectFileComponent, {
      centered: true,
      size: 'lg',
    });
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
  }

  openEnlargeDocumentModal() {
    const modalRef = this.ngbModal.open(EnlargeDocumentComponent, {
      windowClass: 'custom-modal-lg',
    });
    return fromPromise(modalRef.result).pipe(
      catchError(() => EMPTY),
    );
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

  viewDisposition(disposition) {
    this.activeDispositionSubject$.next(disposition.id);
  }

  openRejectModal() {
    this.openRejectModalSubject$.next(true);
  }

  openEnlargeModal() {
    this.openEnlargeDocumentModal();
  }

  downloadFile() {
    this.dispositionsService.downloadData();
  }

  approveActiveDisposition() {
    this.approveDispositionSubject$.next(true);
  }

  hasImageSourceByFileType(fileType) {
    return this.imageFileTypes.includes(fileType);
  }

  getFileTypeIcon(fileType) {
    const keyValue = Object.keys(DocumentFileType).find((key) => DocumentFileType[ key ] === fileType);
    return DocumentFileTypeIcons[ keyValue ];
  }

  getFileExtension(fileName) {
    if (!fileName) {
      return '';
    }
    return fileName.split('.').pop();
  }

  getFileNameWithoutExt(fileName) {
    if (!fileName) {
      return '';
    }
    return fileName.replace(/\.[^/.]+$/, '');
  }
}
