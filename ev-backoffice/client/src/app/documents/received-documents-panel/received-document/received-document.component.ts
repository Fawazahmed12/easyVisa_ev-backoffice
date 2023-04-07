import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { combineLatest, EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, distinctUntilChanged, filter, map, switchMap } from 'rxjs/operators';
import { every, isEqual } from 'lodash-es';
import * as FileSaver from 'file-saver';

import { ModalService, PackagesService, UserService } from '../../../core/services';
import { ConfirmButtonType } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { DocumentsRequestService } from '../../ngrx/services';

import { DocumentPanelType } from '../../models/documents.model';
import { DocumentsService } from '../../services';
import { User } from '../../../core/models/user.model';
import { Role } from '../../../core/models/role.enum';
import { DispositionIncompleteModalComponent } from '../../document-disposition/disposition-incomplete-modal/disposition-incomplete-modal.component';
import { DispositionCompleteModalComponent } from '../../document-disposition/disposition-complete-modal/disposition-complete-modal.component';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import {
  DeleteDocumentAttachmentsFailure,
  PostDocumentActionDateFailure,
  UpdateDocumentApprovalFailure
} from '../../ngrx/documents/documents.actions';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

@Component({
  selector: 'app-received-document',
  templateUrl: './received-document.component.html',
  styleUrls: [ './received-document.component.scss' ]
})
@DestroySubscribers()
export class ReceivedDocumentComponent implements OnInit, OnDestroy {

  @Input() applicantReceivedDocument;
  @Input() readOnlyAccess: boolean;

  dispositionSubmitted = false;
  @ViewChild('removeDocumentAttachmentsModalTemplate') removeDocumentAttachmentsModalTemplate;
  @ViewChild('receivedDocumentAccordion', { static: true }) receivedDocumentAccordion;

  currentUser$: Observable<User>;
  isAttorney$: Observable<boolean>;
  selectedDocumentAttachmentMapper = {};
  documentType: DocumentPanelType;
  dispositionApprovalDocument$: Subject<any> = new Subject<any>();
  dispositionRejectionDocument$: Subject<any> = new Subject<any>();
  downloadDocumentAttachmentListData$: Subject<any> = new Subject<any>();
  deleteDocumentAttachmentListData$: Subject<any> = new Subject<any>();
  dateSelectedDocument$: Subject<any> = new Subject<any>();
  private subscribers: any = {};

  constructor(private userService: UserService,
              private documentsService: DocumentsService,
              private documentsRequestService: DocumentsRequestService,
              private packagesService: PackagesService,
              private modalService: ModalService, private ngbModal: NgbModal) {
  }

  ngOnInit() {
    this.documentsService.openAllReceivedPanels(false);
    this.currentUser$ = this.userService.currentUser$;
    this.isAttorney$ = this.userService.hasAccess([ Role.ROLE_ATTORNEY ]);
    this.documentsService.documentAttachmentsSelection$.subscribe((documentAttachmentMapper) => {
      this.selectedDocumentAttachmentMapper = documentAttachmentMapper;
    });
    this.documentsService.openAllReceivedPanels$.subscribe((opened) => {
      if(opened){
        this.receivedDocumentAccordion.expandAll();
      }else if(!opened && this.receivedDocumentAccordion.activeIds.length){
        this.receivedDocumentAccordion.collapseAll();
      }
    });
    this.documentType = DocumentPanelType.DOCUMENT_RECEIVED_FROM_US;

    this.subscribers.documentAttachmentsDeleteFailureSubscription = this.documentsService.documentAttachmentsDeleteFailAction$
      .pipe(filter((action: DeleteDocumentAttachmentsFailure) => this.documentsService.documentAccessErrorFilter(action)))
      .subscribe((data) => this.documentsService.documentAccessErrorHandler(data));

    this.subscribers.documentApprovalFailureSubscription = this.documentsService.documentApprovalPatchFailAction$
      .pipe(filter((action: UpdateDocumentApprovalFailure) => this.documentsService.documentAccessErrorFilter(action)))
      .subscribe((data) => this.documentsService.documentAccessErrorHandler(data));

    this.subscribers.documentActionDateFailureSubscription = this.documentsService.documentActionDatePostFailAction$
      .pipe(filter((action: PostDocumentActionDateFailure) => this.documentsService.documentAccessErrorFilter(action)))
      .subscribe((data) => this.documentsService.documentAccessErrorHandler(data));

    this.subscribers.saveDocumentApprovalSubscription = combineLatest([
      this.dispositionApprovalDocument$,
      this.packagesService.activePackageId$
    ]).pipe(filter(([ document, packageId ]) => !!document && !!packageId),
      distinctUntilChanged(()=> isEqual && !this.dispositionSubmitted),
      switchMap(([ document, packageId ]) => {
        let modalRef;
        const attachments = document.attachments;
        if (!attachments.length) {
          return;
        }
        const isAllDocAttachmentsApproved = every(attachments, [ 'approved', true ]);
        if (isAllDocAttachmentsApproved) {
          modalRef = this.ngbModal.open(DispositionCompleteModalComponent, {
            backdrop: 'static'
          });
        } else {
          modalRef = this.ngbModal.open(DispositionIncompleteModalComponent, {
            backdrop: 'static'
          });
        }
        const receivedDocumentData = this.getReceivedDocumentData(document);
        const inputData = {
          ...receivedDocumentData,
          packageId,
          isApproved: true
        };
        const newDocument = { ...document, name: document.description };
        modalRef.componentInstance.document = newDocument;
        modalRef.componentInstance.inputData = inputData;
        return of(newDocument);
      }))
      .subscribe((document: any) => {
        this.dispositionSubmitted = false;
        this.resetSelectedDocumentAttachments(document);
      });

    this.subscribers.saveDocumentRejectSubscription = combineLatest([
      this.dispositionRejectionDocument$,
      this.packagesService.activePackageId$
    ]).pipe(filter(([ document, packageId ]) => !!document && !!packageId),
      switchMap(([ document, packageId ]) => {
        const receivedDocumentData = this.getReceivedDocumentData(document);
        const inputData = {
          ...receivedDocumentData,
          packageId,
          isApproved: false
        };
        return this.documentsService.approvedEntireDocumentPanel(inputData).pipe(
          map((_) => document),
          this.showErrorModalWithResponse()
        );
      }))
      .subscribe((document: any) => {
        this.dispositionSubmitted = false;
        this.resetSelectedDocumentAttachments(document);
      });

    this.subscribers.downloadDocumentAttachmentListSubscription = combineLatest([
      this.downloadDocumentAttachmentListData$,
      this.packagesService.activePackageId$
    ]).pipe(filter(([ document, packageId ]) => !!document && !!packageId),
      switchMap(([ document, packageId ]) => {
        const receivedDocumentData = this.getReceivedDocumentData(document);
        const selectedDocumentAttachments = this.selectedDocumentAttachmentMapper[ document.id ] || [];
        const selectedDocumentAttachmentIds = selectedDocumentAttachments.map(data => data.id);
        const inputData = {
          ...receivedDocumentData,
          packageId,
          attachmentIdList: selectedDocumentAttachmentIds
        };
        return this.documentsRequestService.downloadDocumentAttachmentsGetRequest(inputData).pipe(
          map((data) => Object.assign({}, { ...data }, { document })),
          this.showErrorModalWithResponse()
        );
      }))
      .subscribe((data: any) => {
        this.resetSelectedDocumentAttachments(data.document);
        FileSaver.saveAs(data.file, data.fileName);
        this.documentsService.downloadingFiles(false);
      });

    this.subscribers.deleteDocumentAttachmentListSubscription = combineLatest([
      this.deleteDocumentAttachmentListData$,
      this.packagesService.activePackageId$
    ]).pipe(filter(([ document, packageId ]) => !!document && !!packageId),
      switchMap(([ document, packageId ]) => {
        const receivedDocumentData = this.getReceivedDocumentData(document);
        const selectedDocumentAttachments = this.selectedDocumentAttachmentMapper[ document.id ] || [];
        const selectedDocumentAttachmentIds = selectedDocumentAttachments.map(data => data.id);
        const inputData = {
          ...receivedDocumentData,
          packageId,
          attachmentIdList: selectedDocumentAttachmentIds
        };
        return this.documentsService.deleteDocumentAttachmentList(inputData).pipe(
          map((_) => document),
          this.showErrorModalWithResponse()
        );
      }))
      .subscribe((document: any) => {
        this.resetSelectedDocumentAttachments(document);
      });

    this.subscribers.saveDocumentReceivedDateSubscription = combineLatest([
      this.dateSelectedDocument$,
      this.packagesService.activePackageId$
    ]).pipe(filter(([ document, packageId ]) => !!document && !!packageId),
      switchMap(([ dateSelectedDocumentData, packageId ]) => {
        const { document, dateValue } = dateSelectedDocumentData;
        const receivedDocumentData = this.getReceivedDocumentData(document);
        const inputData = {
          ...receivedDocumentData,
          packageId,
          actionDate: dateValue
        };
        return this.documentsService.saveDocumentActionDate(inputData).pipe(
          map((_) => document),
          this.showErrorModalWithResponse()
        );
      }))
      .subscribe((document: any) => {
        this.resetSelectedDocumentAttachments(document);
      });
  }

  getPanelData(document, idx) {
    const receivedDocumentData = this.getReceivedDocumentData(document);
    return {
      ...receivedDocumentData,
      documentId: document.id,
      panelId: this.getPanelId(document, idx)
    };
  }

  getPanelId(document, idx){
    return `${document.id}_${idx}_${this.applicantReceivedDocument.applicantId}`;
  }

  getReceivedDocumentData(document) {
    return {
      applicantId: this.applicantReceivedDocument.applicantId,
      attachmentRefId: document.receivedDocumentType,
      documentType: this.documentType,
    };
  }

  hasSelectedAttachments(document) {
    const selectedAttachments = this.selectedDocumentAttachmentMapper[ document.id ] || [];
    return selectedAttachments.length;
  }

  onRequireDocumentClick(document) {
    this.resetSelectedDocumentAttachments(document);
  }

  onDocumentUpload(document) {
    this.resetSelectedDocumentAttachments(document);
  }

  onReceivedDocumentDelete(document) {
    this.openRemoveDocumentAttachmentsModal()
      .subscribe(() => this.removeReceivedDocumentAttachments(document));
  }

  onReceivedDocumentDownload(document) {
    this.documentsService.downloadingFiles(true);
    this.downloadDocumentAttachmentListData$.next(document);
  }

  openRemoveDocumentAttachmentsModal() {
    const buttons = [
      {
        label: 'FORM.BUTTON.CANCEL',
        type: ConfirmButtonType.Dismiss,
        className: 'btn btn-primary mr-4 min-w-100',
      },
      {
        label: 'FORM.BUTTON.CONFIRM',
        type: ConfirmButtonType.Close,
        className: 'btn btn-primary mr-4 min-w-100',
      },
    ];

    return this.modalService.openConfirmModal({
      header: 'Delete Document Warning',
      body: this.removeDocumentAttachmentsModalTemplate,
      buttons,
      centered: true,
    });
  }

  removeReceivedDocumentAttachments(document) {
    this.deleteDocumentAttachmentListData$.next(document);
  }

  resetSelectedDocumentAttachments(document) {
    this.documentsService.resetSelectedDocumentAttachments(document);
  }

  onDateSelection(e, document) {
    const dateValue: string = e.month + '-' + e.day + '-' + e.year;
    this.dateSelectedDocument$.next({ document, dateValue });
  }

  onDispositionApprovedEntirePanel(document) {
    this.dispositionSubmitted = true;
    this.dispositionApprovalDocument$.next(document);
  }

  onDispositionRejectEntirePanel(document) {
    this.dispositionSubmitted = true;
    this.dispositionRejectionDocument$.next(document);
  }

  showErrorModalWithResponse() {
    return (observable) => observable.pipe(
      catchError((error: HttpErrorResponse) => {
          if (error.status !== 401) {
            this.modalService.showErrorModal(error.error.errors || [ error.error ]);
          }
          return EMPTY;
        }
      ),
    );
  }

  ngOnDestroy() {
    if (this.subscribers.deleteDocumentAttachmentListSubscription) {
      this.subscribers.deleteDocumentAttachmentListSubscription.unsubscribe();
      this.subscribers.deleteDocumentAttachmentListSubscription = null;
    }
    if (this.subscribers.downloadDocumentAttachmentListSubscription) {
      this.subscribers.downloadDocumentAttachmentListSubscription.unsubscribe();
      this.subscribers.downloadDocumentAttachmentListSubscription = null;
    }
    if (this.subscribers.saveDocumentReceivedDateSubscription) {
      this.subscribers.saveDocumentReceivedDateSubscription.unsubscribe();
      this.subscribers.saveDocumentReceivedDateSubscription = null;
    }
    if (this.subscribers.saveDocumentApprovalSubscription) {
      this.subscribers.saveDocumentApprovalSubscription.unsubscribe();
      this.subscribers.saveDocumentApprovalSubscription = null;
    }
    if (this.subscribers.saveDocumentRejectSubscription) {
      this.subscribers.saveDocumentRejectSubscription.unsubscribe();
      this.subscribers.saveDocumentRejectSubscription = null;
    }
    if (this.subscribers.documentAttachmentsDeleteFailureSubscription) {
      this.subscribers.documentAttachmentsDeleteFailureSubscription.unsubscribe();
      this.subscribers.documentAttachmentsDeleteFailureSubscription = null;
    }
    if (this.subscribers.documentApprovalFailureSubscription) {
      this.subscribers.documentApprovalFailureSubscription.unsubscribe();
      this.subscribers.documentApprovalFailureSubscription = null;
    }
    if (this.subscribers.documentActionDateFailureSubscription) {
      this.subscribers.documentActionDateFailureSubscription.unsubscribe();
      this.subscribers.documentActionDateFailureSubscription = null;
    }
  }
}
