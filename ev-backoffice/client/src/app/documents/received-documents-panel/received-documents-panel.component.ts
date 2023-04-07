import { Component, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ModalService, PackagesService, UserService } from '../../core/services';
import { ApplicantReceivedDocuments } from '../models/received-document.model';
import { EMPTY, Observable, combineLatest, Subject } from 'rxjs';
import { catchError, filter, map, switchMap } from 'rxjs/operators';
import { DocumentsService } from '../services';
import { HttpErrorResponse } from '@angular/common/http';
import { ApplicantType } from '../../core/models/applicantType.enum';
import { DocumentPanelType } from '../models/documents.model';
import * as FileSaver from 'file-saver';
import { DocumentsRequestService } from '../ngrx/services';

@Component({
  selector: 'app-received-documents-panel',
  templateUrl: './received-documents-panel.component.html',
  styleUrls: ['./received-documents-panel.component.scss']
})
export class ReceivedDocumentsPanelComponent implements OnInit, OnDestroy {

  @Input() readOnlyAccess: boolean;
  applicantReceivedDocumentList$: Observable<ApplicantReceivedDocuments[]>;
  downloadDocumentAttachmentListData$: Subject<any> = new Subject<any>();
  private subscribers: any = {};
  beneficiaryName$: Observable<string>;
  isOpenedAllPanels$: Observable<boolean> ;

  constructor(private userService: UserService,
              private documentsService: DocumentsService,
              private packagesService: PackagesService,
              private documentsRequestService: DocumentsRequestService,
              private router: Router, private modalService: ModalService) {
  }

  ngOnInit() {
    this.isOpenedAllPanels$ = this.documentsService.openAllReceivedPanels$;
    this.beneficiaryName$ = this.packagesService.activePackage$.pipe(
      filter((activePackage) => !!activePackage),
      map((activePackage) => activePackage.applicants.find(
        (applicant) => applicant.applicantType === ApplicantType.BENEFICIARY
          || applicant.applicantType === ApplicantType.PRINCIPAL_BENEFICIARY).profile
      ),
      map((beneficiaryProfile) => `${beneficiaryProfile.firstName} ${beneficiaryProfile.lastName}`)
    );
    this.applicantReceivedDocumentList$ = this.documentsService.applicantReceivedDocuments$.pipe(
      filter((applicantReceivedDocuments) => !!applicantReceivedDocuments),
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
          documentType: DocumentPanelType.DOCUMENT_RECEIVED_FROM_US
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
    this.documentsService.openAllReceivedPanels(true);
  }

  closeAllPanels(){
    this.documentsService.openAllReceivedPanels(false);
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
