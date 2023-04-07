import { Component, OnDestroy, OnInit } from '@angular/core';

import { NgbActiveModal, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, map, switchMap, withLatestFrom } from 'rxjs/operators';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';
import { fromPromise } from 'rxjs/internal-compatibility';

import { EmailTemplatesService, OrganizationService } from '../../../core/services';
import { RequestState } from '../../../core/ngrx/utils';
import { DocumentFileType, DocumentFileTypeIcons, DocumentImageFileType } from '../../../documents/models/documents.model';
import { EmployeePosition } from '../../../account/permissions/models/employee-position.enum';

import { Disposition } from '../../models/dispositions.model';
import { DispositionData } from '../../models/disposition-data.model';

import { DispositionsService } from '../dispositions.service';
import { RejectFileComponent } from '../reject-file/reject-file.component';
import { Organization } from '../../../core/models/organization.model';


@Component({
  selector: 'app-enlarge-document',
  templateUrl: './enlarge-document.component.html',
  styleUrls: ['./enlarge-document.component.scss']
})
@DestroySubscribers()
export class EnlargeDocumentComponent implements OnInit, AddSubscribers, OnDestroy {
  activeOrganization$: Observable<Organization>;
  activeDisposition$: Observable<Disposition>;
  dispositions$: Observable<Disposition[]>;
  activeDispositionData$: Observable<DispositionData>;
  openRejectModalSubject$: Subject<boolean> = new Subject<boolean>();
  getDispositionDataGetRequest$: Observable<RequestState<DispositionData>>;
  getDispositionPutRequest$: Observable<RequestState<Disposition>>;
  approveDispositionSubject$: Subject<boolean> = new Subject<boolean>();

  imageFileTypes = Object.values(DocumentImageFileType);
  EmployeePosition = EmployeePosition;

  private subscribers: any = {};

  constructor(
    private emailTemplatesService: EmailTemplatesService,
    private dispositionsService: DispositionsService,
    private organizationService: OrganizationService,
    private activeModal: NgbActiveModal,
    private ngbModal: NgbModal,
  ) {
  }

  ngOnInit() {
    this.activeOrganization$ = this.organizationService.activeOrganization$;
    this.activeDisposition$ = this.dispositionsService.activeDisposition$;
    this.dispositions$ = this.dispositionsService.dispositions$;
    this.activeDispositionData$ = this.dispositionsService.activeDispositionData$;
    this.getDispositionDataGetRequest$ = this.dispositionsService.getDispositionDataGetRequest$;
    this.getDispositionPutRequest$ = this.dispositionsService.getDispositionPutRequest$;
  }

  addSubscribers() {

    this.subscribers.openRejectModalSubscription = this.openRejectModalSubject$.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
      map(([ , [ representativeId, ]]) => {
        this.dispositionsService.getEmailTemplateForPopUp(representativeId);
        return representativeId;
      }),
      switchMap((representativeId) => this.openRejectDocumentModal().pipe(
        map(data => [ data, representativeId ])
      )),
    ).subscribe(([ data, representativeId ]) =>
      this.dispositionsService.rejectDisposition({
        approve: false,
        rejectionMailMessage: data.emailTemplate,
        rejectionMailSubject: data.subject,
        organizationId: data.organizationId,
        representativeId
      })
    );

    this.subscribers.activeDispositionSubscription = this.dispositions$.pipe(
      filter((dispositions) => !!dispositions),
      filter((dispositions) => dispositions.length === 0),
    ).subscribe(() => this.activeModal.dismiss());

    this.subscribers.approveDispositionSubscription = this.approveDispositionSubject$.pipe(
      withLatestFrom(this.organizationService.currentRepIdOrgId$),
    ).subscribe(([ ,[ representativeId, organizationId ]]) => {
      this.dispositionsService.approveDisposition({
        approve: true,
        organizationId,
        representativeId,
      });
    });
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  downloadFile() {
    this.dispositionsService.downloadData();
  }

  approveActiveDisposition() {
    this.approveDispositionSubject$.next(true);
  }

  nextActive() {
    this.dispositionsService.setNextId();
  }

  previousActive() {
    this.dispositionsService.setPreviousId();
  }

  openRejectModal() {
    this.openRejectModalSubject$.next(true);
  }

  hasImageSourceByFileType(fileType) {
    return this.imageFileTypes.includes(fileType);
  }

  getFileTypeIcon(fileType) {
    const keyValue = Object.keys(DocumentFileType).find((key) => DocumentFileType[key] === fileType);
    return DocumentFileTypeIcons[ keyValue ];
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
}
