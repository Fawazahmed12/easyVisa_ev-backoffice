import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { EMPTY, Observable, combineLatest, Subject } from 'rxjs';

import { DocumentsService } from '../services';
import { ApplicantSentDocuments } from '../models/sent-document.model';
import { ModalService, PackagesService, UserService } from '../../core/services';
import { catchError, filter, map, switchMap } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { DocumentPanelType } from '../models/documents.model';
import * as FileSaver from 'file-saver';
import { DocumentsRequestService } from '../ngrx/services';

@Component({
  selector: 'app-sent-documents-panel',
  templateUrl: './sent-documents-panel.component.html',
  styleUrls: [ './sent-documents-panel.component.scss' ]
})
export class SentDocumentsPanelComponent implements OnInit, OnDestroy {

  @Input() readOnlyAccess: boolean;
  applicantSentDocumentList$: Observable<ApplicantSentDocuments[]>;
  downloadDocumentAttachmentListData$: Subject<any> = new Subject<any>();
  private subscribers: any = {};
  isOpenedAllPanels$: Observable<boolean> ;

  constructor(private userService: UserService,
              private documentsService: DocumentsService,
              private router: Router,
              private documentsRequestService: DocumentsRequestService,
              private packagesService: PackagesService,
              private modalService: ModalService) {
  }

  ngOnInit() {
    this.isOpenedAllPanels$ = this.documentsService.openAllSentPanels$;
    this.applicantSentDocumentList$ = this.documentsService.applicantSentDocuments$.pipe(
      filter((applicantSentDocuments) => !!applicantSentDocuments),
      catchError((error: HttpErrorResponse) => {
          this.modalService.showErrorModal(error.error.errors || [ error.error ]);
          return EMPTY;
        }
      )
    );

    this.subscribers.downloadAllDocumentAttachmentsSubscription = combineLatest([
      this.downloadDocumentAttachmentListData$,
      this.packagesService.activePackageId$
    ]).pipe(filter(([ applicantRequiredDocument, packageId ]) => !!applicantRequiredDocument && !!packageId),
      switchMap(([ applicantRequiredDocument, packageId ]) => {
        const inputData = {
          packageId,
          applicantId: applicantRequiredDocument.applicantId,
          documentType: DocumentPanelType.DOCUMENT_SENT_TO_US
        };
        return this.documentsRequestService.downloadAllDocumentAttachmentsGetRequest(inputData).pipe(
          map((data) => Object.assign({}, { ...data })),
          this.showErrorModalWithResponse()
        );
      }))
      .subscribe((data: any) => {
        FileSaver.saveAs(data.file, data.fileName);
        this.documentsService.downloadingFiles(false);
      });
  }

  downloadAllAttachments(applicantRequiredDocument){
    this.documentsService.downloadingFiles(true);
    this.downloadDocumentAttachmentListData$.next(applicantRequiredDocument);
  }

  openAllPanels(){
    this.documentsService.openAllSentPanels(true);
  }

  closeAllPanels(){
    this.documentsService.openAllSentPanels(false);
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
    if (this.subscribers.downloadAllDocumentAttachmentsSubscription) {
      this.subscribers.downloadAllDocumentAttachmentsSubscription.unsubscribe();
      this.subscribers.downloadAllDocumentAttachmentsSubscription = null;
    }
  }

}
