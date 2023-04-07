import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { combineLatest, EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, startWith, switchMap, withLatestFrom } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { DocumentFileType } from '../models/documents.model';

import { ModalService, NotificationsService, OrganizationService, PackagesService } from '../../core/services';
import { DocumentsService } from '../services';
import { PostDocumentAttachmentUploadFailure } from '../ngrx/documents/documents.actions';


@Component({
  selector: 'app-upload-document',
  templateUrl: './upload-document.component.html',
  styleUrls: [ './upload-document.component.scss' ]
})

@DestroySubscribers()
export class UploadDocumentComponent implements OnInit, AddSubscribers {

  @Output() documentUploadEmitter = new EventEmitter();
  @Input() panelData;
  @Input() readOnlyAccess: boolean;

  activePackageId$: Observable<number>;
  uploadDocumentSubject$: Subject<FormData> = new Subject();
  uploadDocumentCompleteSubject$: Subject<boolean> = new Subject();

  fileTypes = [];
  acceptFileTypes = '';
  private subscribers: any = {};

  constructor(private modalService: ModalService,
              private packagesService: PackagesService,
              private documentsService: DocumentsService,
              private organizationService: OrganizationService,
              private notificationsService: NotificationsService,
  ) {
  }

  ngOnInit() {
    this.fileTypes = Object.values(DocumentFileType);
    this.acceptFileTypes = this.fileTypes.toString();
  }

  addSubscribers() {
    this.subscribers.taskQueueNotificationsSubscription = combineLatest([
      this.organizationService.currentRepresentativeId$.pipe(
        filter(val => val !== undefined),
        startWith(null)
      ),
      this.organizationService.activeOrganizationId$.pipe(
        filter(id => !!id),
        startWith(null)
      ),
      this.organizationService.withoutOrganizations$,
      this.uploadDocumentCompleteSubject$
    ]).subscribe(([ representativeId, organizationId, withoutOrganizations, hasCompleted ]: [ number, string, boolean, boolean ]) => {
      if (!withoutOrganizations) {
        this.notificationsService.getTaskQueueNotifications({ representativeId, organizationId });
      }
    });

    this.subscribers.uploadDocumentSubscription = this.uploadDocumentSubject$.pipe(
      withLatestFrom(this.packagesService.activePackageId$),
      filter(([ fileData, packageId ]) => !!fileData && !!packageId),
      switchMap(([ fileData, packageId ]: [ FormData, number ]) => {
          fileData.append('packageId', JSON.stringify(packageId));
          fileData.append('applicantId', this.panelData.applicantId);
          fileData.append('attachmentRefId', this.panelData.attachmentRefId);
          fileData.append('documentType', this.panelData.documentType);
          return this.documentsService.uploadDocumentFile(fileData).pipe(
            this.showErrorModalWithResponse()
          );
        }
      )
    ).subscribe(() => {
      this.documentUploadEmitter.emit();
      this.uploadDocumentCompleteSubject$.next(true);
    });

    this.subscribers.uploadDocumentFailureSubscription = this.documentsService.documentAttachmentPostFailAction$
      .pipe(
        filter((action: PostDocumentAttachmentUploadFailure) => this.documentsService.documentAccessErrorFilter(
          action,
          this.panelData.attachmentRefId
        ))
      )
      .subscribe((data) => this.documentsService.documentAccessErrorHandler(data));
  }

  documentUpload(event) {
    const file: File = event.target.files[ 0 ];
    if (file) {
      const formData: FormData = new FormData();
      formData.append('attachment', file);
      this.uploadDocumentSubject$.next(formData);
    }
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

}
