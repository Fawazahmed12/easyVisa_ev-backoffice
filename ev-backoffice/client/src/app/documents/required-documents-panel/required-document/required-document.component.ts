import { Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { combineLatest, EMPTY, Observable, of, Subject } from 'rxjs';

import { catchError, distinctUntilChanged, filter, map, switchMap } from 'rxjs/operators';

import { DestroySubscribers } from 'ngx-destroy-subscribers';
import * as FileSaver from 'file-saver';
import { every, isEqual } from 'lodash-es';

import { DocumentPanelType } from '../../models/documents.model';
import { DocumentsService } from '../../services';
import { ModalService, PackagesService, UserService } from '../../../core/services';
import { ConfirmButtonType } from '../../../core/modals/confirm-modal/confirm-modal.component';
import { DocumentsRequestService } from '../../ngrx/services';
import { Role } from '../../../core/models/role.enum';
import { User } from '../../../core/models/user.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { DispositionCompleteModalComponent } from '../../document-disposition/disposition-complete-modal/disposition-complete-modal.component';
import { DispositionIncompleteModalComponent } from '../../document-disposition/disposition-incomplete-modal/disposition-incomplete-modal.component';
import {
  DeleteDocumentAttachmentsFailure,
  UpdateDocumentApprovalFailure
} from '../../ngrx/documents/documents.actions';
import { RequiredDocumentHelpComponent } from '../required-document-help/required-document-help.component';


@Component({
  selector: 'app-required-document',
  templateUrl: './required-document.component.html',
  styleUrls: [ './required-document.component.scss' ]
})

@DestroySubscribers()
export class RequiredDocumentComponent implements OnInit, OnDestroy {

  currentUser$: Observable<User>;
  isAttorney$: Observable<boolean>;

  dispositionSubmitted = false;

  @Input() applicantRequiredDocument;
  @Input() readOnlyAccess: boolean;
  @ViewChild('removeDocumentAttachmentsModalTemplate', { static: true }) removeDocumentAttachmentsModalTemplate;

  selectedDocumentAttachmentMapper = {};
  documentType: DocumentPanelType;
  dispositionApprovalDocument$: Subject<any> = new Subject<any>();
  dispositionRejectionDocument$: Subject<any> = new Subject<any>();
  downloadDocumentAttachmentListData$: Subject<any> = new Subject<any>();
  deleteDocumentAttachmentListData$: Subject<any> = new Subject<any>();
  private subscribers: any = {};

  constructor(private userService: UserService,
              private documentsService: DocumentsService,
              private documentsRequestService: DocumentsRequestService,
              private packagesService: PackagesService,
              private modalService: ModalService, private ngbModal: NgbModal) {
  }

  ngOnInit() {
    this.currentUser$ = this.userService.currentUser$;
    this.isAttorney$ = this.userService.hasAccess([ Role.ROLE_ATTORNEY ]);
    this.documentsService.documentAttachmentsSelection$.subscribe((documentAttachmentMapper) => {
      this.selectedDocumentAttachmentMapper = documentAttachmentMapper;
    });
    this.documentType = DocumentPanelType.REQUIRED_DOCUMENT;

    this.subscribers.documentAttachmentsDeleteFailureSubscription = this.documentsService.documentAttachmentsDeleteFailAction$
      .pipe(filter((action: DeleteDocumentAttachmentsFailure) => this.documentsService.documentAccessErrorFilter(action)))
      .subscribe((data) => this.documentsService.documentAccessErrorHandler(data));

    this.subscribers.documentApprovalFailureSubscription = this.documentsService.documentApprovalPatchFailAction$
      .pipe(filter((action: UpdateDocumentApprovalFailure) => this.documentsService.documentAccessErrorFilter(action)))
      .subscribe((data) => this.documentsService.documentAccessErrorHandler(data));

    this.subscribers.saveDocumentApprovalSubscription = combineLatest([
      this.dispositionApprovalDocument$,
      this.packagesService.activePackageId$
    ]).pipe(
      filter(([ document, packageId ]) => !!document && !!packageId),
      distinctUntilChanged(() => isEqual && !this.dispositionSubmitted),
      switchMap(([ document, packageId ]) => {
        let modalRef;
        const attachments = document.attachments || [];
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
        const requiredDocumentData = this.getRequiredDocumentData(document);
        const inputData = {
          ...requiredDocumentData,
          packageId,
          isApproved: true
        };
        modalRef.componentInstance.document = document;
        modalRef.componentInstance.inputData = inputData;
        return of(document);
      }))
      .subscribe((document: any) => {
        this.dispositionSubmitted = false;
        this.resetSelectedDocumentAttachments(document);
      });

    this.subscribers.saveDocumentRejectSubscription = combineLatest([
      this.dispositionRejectionDocument$,
      this.packagesService.activePackageId$
    ]).pipe(
      filter(([ document, packageId ]) => !!document && !!packageId),
      switchMap(([ document, packageId ]) => {
        const requiredDocumentData = this.getRequiredDocumentData(document);
        const inputData = {
          ...requiredDocumentData,
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
        const requiredDocumentData = this.getRequiredDocumentData(document);
        const selectedDocumentAttachments = this.selectedDocumentAttachmentMapper[ document.id ] || [];
        const selectedDocumentAttachmentIds = selectedDocumentAttachments.map(data => data.id);
        const inputData = {
          ...requiredDocumentData,
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
        const requiredDocumentData = this.getRequiredDocumentData(document);
        const selectedDocumentAttachments = this.selectedDocumentAttachmentMapper[ document.id ] || [];
        const selectedDocumentAttachmentIds = selectedDocumentAttachments.map(data => data.id);
        const inputData = {
          ...requiredDocumentData,
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
  }

  getPanelData(document, idx) {
    const requiredDocumentData = this.getRequiredDocumentData(document);
    return {
      ...requiredDocumentData,
      documentId: document.id,
      panelId: this.getPanelId(document, idx)
    };
  }

  getPanelId(document, idx){
    return `${document.id}_${idx}_${this.applicantRequiredDocument.applicantId}`;
  }

  getRequiredDocumentData(document) {
    return {
      applicantId: this.applicantRequiredDocument.applicantId,
      attachmentRefId: document.id,
      documentType: this.documentType
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

  resetSelectedDocumentAttachments(document) {
    this.documentsService.resetSelectedDocumentAttachments(document);
  }

  onDispositionApprovedEntirePanel(document) {
    this.dispositionSubmitted = true;
    this.dispositionApprovalDocument$.next(document);
  }

  onDispositionRejectEntirePanel(document) {
    this.dispositionSubmitted = true;
    this.dispositionRejectionDocument$.next(document);
  }

  onRequiredDocumentDelete(document) {
    this.openRemoveDocumentAttachmentsModal().subscribe(() => this.removeRequiredDocumentAttachments(document));
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

  removeRequiredDocumentAttachments(document) {
    this.deleteDocumentAttachmentListData$.next(document);
  }

  onRequiredDocumentDownload(document) {
    this.documentsService.downloadingFiles(true);
    this.downloadDocumentAttachmentListData$.next(document);
  }

  openRequireDocumentHelp(document) {
    const modalRef = this.ngbModal.open(RequiredDocumentHelpComponent, {
      size: 'lg',
      centered: true,
      windowClass: 'document-help-viewer-modal'
    });
    modalRef.componentInstance.requiredDocument = document;
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
  }

}
