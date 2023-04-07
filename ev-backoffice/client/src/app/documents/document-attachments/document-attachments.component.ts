import { AfterViewChecked, Component, Input, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { combineLatest, EMPTY, Subject } from 'rxjs';
import { catchError, filter, switchMap } from 'rxjs/operators';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { SelectContainerComponent } from 'ngx-drag-to-select';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { ModalService, PackagesService } from '../../core/services';
import { DocumentFileType, DocumentFileTypeIcons } from '../models/documents.model';
import { DocumentsRequestService } from '../ngrx/services';
import { DocumentsService } from '../services';

import { DocumentViewerModalComponent } from '../document-viewer-modal/document-viewer-modal.component';

const SPINNER_SRC = '../../../../assets/images/spinner.gif';

@Component({
  selector: 'app-document-attachments',
  templateUrl: './document-attachments.component.html',
  styleUrls: [ './document-attachments.component.scss' ]
})
@DestroySubscribers()
export class DocumentAttachmentsComponent implements OnInit, AfterViewChecked, OnDestroy {

  @Input() document; // It can be instance of RequiredDocument or SignedDocument or ReceivedDocument
  @Input() documentType;
  @Input() applicantId;
  @Input() readOnlyAccess;
  @ViewChild(SelectContainerComponent) selectContainer: SelectContainerComponent;

  selectedDocumentAttachments = [];
  selectedItems = [];

  imageFileTypes = [
    DocumentFileType.bmp,
    DocumentFileType.bmp1,
    DocumentFileType.jpeg,
    DocumentFileType.jpeg1,
    DocumentFileType.jpg,
    DocumentFileType.jpg1,
    DocumentFileType.png
  ];

  private attachmentThumbnailMapper: any = {};

  attachmentPreviewDocument$: Subject<any> = new Subject<any>();

  private subscribers: any = {};

  constructor(private ngbModal: NgbModal,
              private modalService: ModalService,
              private packagesService: PackagesService,
              private documentsRequestService: DocumentsRequestService,
              private documentsService: DocumentsService) {

  }

  ngOnInit() {

    this.subscribers.activePackageIdSubscription = this.packagesService.activePackageId$.subscribe((packageId) => {
      this.document.attachments.forEach((attachment, index) => {
        if (this.hasImageSourceByFileType(attachment.fileType)) {
          this.fetchThumbnailContent(packageId, attachment, index);
        }
      });
    });

    this.subscribers.documentAttachmentsSelectionSubscription = this.documentsService.documentAttachmentsSelection$
      .subscribe((documentAttachmentMapper) => {
        this.selectedDocumentAttachments = documentAttachmentMapper[ this.document.id ] || [];
      });

    this.subscribers.documentAttachmentPreviewSubscription = combineLatest([
      this.attachmentPreviewDocument$,
      this.packagesService.activePackageId$
    ]).pipe(filter(([ attachment, packageId ]) => !!attachment && !!packageId),
      switchMap(([ attachment, packageId ]) => {
        const inputData = {
          packageId,
          applicantId: this.applicantId,
          attachmentRefId: this.document.id,
          attachmentId: attachment.id,
          documentType: this.documentType
        };
        return this.documentsRequestService.previewDocumentAttachment(inputData);
      }))
      .subscribe(
        data => this.openDocumentViewerModal(data),
        error => this.showErrorModalWithResponse()
      );
  }

  ngAfterViewChecked() {
    this.selectContainer && this.selectContainer.update();
  }

  onDocumentSelect(selectedAttachments) {
    this.documentsService.selectDocumentAttachment({
      document: this.document,
      attachments: [ ...selectedAttachments ] || []
    });
  }

  onDocumentAttachmentPreview(attachment) {
    this.attachmentPreviewDocument$.next(attachment);
  }

  fetchThumbnailContent(packageId, attachment, index) {
    const inputData = {
      packageId,
      applicantId: this.applicantId,
      attachmentRefId: this.document.id,
      attachmentId: attachment.id,
      documentType: this.documentType
    };
    this.subscribers[ `fetchThumbnailAttachmentSubscription${index}` ] = this.documentsRequestService.fetchThumbnailAttachment(inputData).pipe(
      filter((data) => !!data))
      .subscribe((data) => {
        const file = data.file;
        if (file.size) {
          const fileType = file.type;
          const fileBlob = new Blob([ file ], { type: DocumentFileType[ fileType ] });
          this.attachmentThumbnailMapper[ attachment.id ] = URL.createObjectURL(fileBlob);
        }
      });
  }

  getThumbnailUrl(attachment) {
    return this.attachmentThumbnailMapper[ attachment.id ] || SPINNER_SRC;
  }

  hasImageThumbnail(attachment) {
    return this.attachmentThumbnailMapper[ attachment.id ];
  }

  getSelectedDocumentsClass(attachment) {
    const selectedDocument = this.selectedDocumentAttachments.find((selectedDocumentAttachment) => selectedDocumentAttachment === attachment);
    return selectedDocument ? 'selected' : '';
  }

  getFileTypeIcon(fileType) {
    const fileTypeStr = fileType.toLowerCase();
    return DocumentFileTypeIcons[ fileTypeStr ];
  }

  openDocumentViewerModal(data) {
    const file = data.file;
    const fileType = data.fileType;
    const fileBlob = new Blob([ file ], { type: DocumentFileType[ fileType ] });
    const fileURL = URL.createObjectURL(fileBlob);

    if (this.hasPdfFileType(DocumentFileType[ fileType ])) {
      const pdfWindow = window.open('', data.fileName, 'menubar=0,location=0,toolbar=0,resizable=1,status=1,scrollbars=1,width=1100,height=700');
      if (pdfWindow.document) {
        const template = `<html>
                          <head><title>${data.fileName}</title></head>
                          <body height="100%" width="100%" style="margin: 0;padding: 0;">
                            <iframe src="${fileURL}" height="100%" width="100%" frameborder="0"
                            style="border:none;"></iframe>
                          </body></html>`;
        pdfWindow.document.write(template);
      }
      return true;
    } else {
      const modalRef = this.ngbModal.open(DocumentViewerModalComponent, {
        size: 'lg',
        backdrop: 'static',
        windowClass: 'document-attachment-viewer-modal'
      });
      modalRef.componentInstance.documentFileData = {
        fileURL,
        fileName: data.fileName,
        fileType,
        fileContentType: DocumentFileType[ fileType ]
      };
      modalRef.componentInstance.readOnlyAccess = this.readOnlyAccess;
    }
  }

  hasPdfFileType(fileContentType) {
    return fileContentType === DocumentFileType.pdf;
  }

  hasImageSourceByFileType(fileType) {
    const fileContentType = DocumentFileType[ fileType ];
    return this.imageFileTypes.indexOf(fileContentType) !== -1;
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
    console.log(`${this.constructor.name} Destroys`);
  }
}
