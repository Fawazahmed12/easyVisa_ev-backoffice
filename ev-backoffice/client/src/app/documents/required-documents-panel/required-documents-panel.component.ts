import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { EMPTY, Observable, combineLatest, Subject } from 'rxjs';
import { catchError, filter, map, switchMap } from 'rxjs/operators';

import { DocumentsService } from '../services';

import { ModalService, PackagesService } from '../../core/services';
import { DocumentPanelType, RequiredApplicantDocumentModel } from '../models/documents.model';

import * as FileSaver from 'file-saver';
import { DocumentsRequestService } from '../ngrx/services';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

@Component({
  selector: 'app-required-documents-panel',
  templateUrl: './required-documents-panel.component.html',
  styleUrls: [ './required-documents-panel.component.scss' ]
})

@DestroySubscribers()
export class RequiredDocumentsPanelComponent implements OnInit, OnDestroy {

  @Input() readOnlyAccess: boolean;
  applicantRequiredDocumentList$: Observable<RequiredApplicantDocumentModel[]>;
  downloadDocumentAttachmentListData$: Subject<any> = new Subject<any>();
  private subscribers: any = {};

  constructor(private documentsService: DocumentsService,
              private router: Router,
              private documentsRequestService: DocumentsRequestService,
              private packagesService: PackagesService,
              private modalService: ModalService) {
  }

  ngOnInit() {
    this.applicantRequiredDocumentList$ = this.documentsService.requiredApplicantDocuments$.pipe(
      filter((requiredDocuments) => !!requiredDocuments),
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
          documentType: DocumentPanelType.REQUIRED_DOCUMENT
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
