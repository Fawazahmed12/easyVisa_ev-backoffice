import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';

import { combineLatest, EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, startWith, switchMap, withLatestFrom } from 'rxjs/operators';
import { FileSystemFileEntry, NgxFileDropEntry } from 'ngx-file-drop';
import { DestroySubscribers } from 'ngx-destroy-subscribers';

import { DocumentsService } from '../services';
import { DocumentFileType } from '../models/documents.model';

import { validateFileSize } from '../../shared/utils/validate-file-size';
import { checkAllowedFileType } from '../../shared/utils/check-allowed-file-type';
import { ModalService, NotificationsService, OrganizationService, PackagesService } from '../../core/services';
import { PostDocumentAttachmentUploadFailure } from '../ngrx/documents/documents.actions';


@Component({
  selector: 'app-drag-drop-document',
  templateUrl: './drag-drop-document.component.html',
  styleUrls: [ './drag-drop-document.component.scss' ]
})

@DestroySubscribers()
export class DragDropDocumentComponent implements OnInit {

  @Output() documentDropEmitter = new EventEmitter();
  @Input() panelData;
  @Input() accordionRef;
  @Input() readOnlyAccess: boolean;
  fileTypes = [];
  activePackageId$: Observable<number>;
  isFileNotAllowed$: Subject<boolean> = new Subject<boolean>();
  isFileSizeNotAllowed$: Subject<boolean> = new Subject<boolean>();
  uploadDocumentSubject$: Subject<FormData> = new Subject();
  uploadDocumentCompleteSubject$: Subject<boolean> = new Subject();
  private subscribers: any = {};

  constructor(private modalService: ModalService,
              private packagesService: PackagesService,
              private documentsService: DocumentsService,
              private organizationService: OrganizationService,
              private notificationsService: NotificationsService) {
  }

  ngOnInit() {
    this.fileTypes = Object.values(DocumentFileType);
  }

  public dropped(files: NgxFileDropEntry[]) {
    const droppedFile = files[ 0 ];
    if (droppedFile.fileEntry.isFile) {
      const fileEntry = droppedFile.fileEntry as FileSystemFileEntry;
      fileEntry.file((file: File) => {
        const fileType = file.type || this.getFileTypeByName(file.name);
        const isFileAllowed = checkAllowedFileType(fileType, this.fileTypes);
        this.isFileNotAllowed$.next(!isFileAllowed);
        const isFileSizeAllowed = validateFileSize(file.size);
        this.isFileSizeNotAllowed$.next(!isFileSizeAllowed);
        if (file && isFileAllowed && isFileSizeAllowed) {
          const formData: FormData = new FormData();
          formData.append('attachment', file);
          this.uploadDocumentSubject$.next(formData);
        }
      });
    }
    this.accordionRef._element.nativeElement.classList.remove('dragging-z-index');
  }

  public fileOver(event) {
    this.accordionRef._element.nativeElement.classList.add('dragging-z-index');
    this.accordionRef.expand(this.panelData.panelId);
  }

  public fileLeave(event) {
    this.accordionRef._element.nativeElement.classList.remove('dragging-z-index');
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
      this.documentDropEmitter.emit();
      this.uploadDocumentCompleteSubject$.next(true);
    });

    this.subscribers.uploadDocumentFailureSubscription = this.documentsService.documentAttachmentPostFailAction$
      .pipe(
        filter((action: PostDocumentAttachmentUploadFailure) => this.documentsService.documentAccessErrorFilter(
          action,
          this.panelData.attachmentRefId)
        )
      )
      .subscribe((data) => this.documentsService.documentAccessErrorHandler(data));
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

  private getFileTypeByName(fileName: string): string {
    const dotIndex = fileName.lastIndexOf('.');
    const fileExt = fileName.substring(dotIndex + 1).toLowerCase();
    return DocumentFileType[ fileExt ];
  }

}
