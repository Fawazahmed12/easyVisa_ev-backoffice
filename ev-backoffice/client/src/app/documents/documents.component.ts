import { Component, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { DocumentsService } from './services';
import { ActivePackageComponent } from '../components/active-package/active-package.component';
import { NotificationsService } from '../core/services';
import { DocumentPortalAccessState,DocumentAccessState } from './models/documents.model';


@Component({
  selector: 'app-documents',
  templateUrl: './documents.component.html',
  styleUrls: [ './documents.component.scss' ]
})
export class DocumentsComponent implements OnInit, OnDestroy {

  fileUploading$: Observable<boolean>;
  fileDownloading$: Observable<boolean>;
  documentPortalAccessData$: Observable<DocumentPortalAccessState>;
  requiredDocumentsGetRequest$;
  sentDocumentsGetRequest$;
  receivedDocumentsGetRequest$;
  documentAccessData$: Observable<DocumentAccessState>;

  constructor(
    private documentService: DocumentsService,
    private notificationsService: NotificationsService,
  ) {
  }

  ngOnInit() {
    this.requiredDocumentsGetRequest$ = this.documentService.requiredDocumentsGetRequest$;
    this.sentDocumentsGetRequest$ = this.documentService.sentDocumentsGetRequest$;
    this.receivedDocumentsGetRequest$ = this.documentService.receivedDocumentsGetRequest$;
    this.documentPortalAccessData$ = this.documentService.documentPortalAccessData$;
    this.notificationsService.showComponent$.next(ActivePackageComponent);
    this.fileUploading$ = this.documentService.fileUploading$;
    this.fileDownloading$ = this.documentService.fileDownloading$;
    this.documentAccessData$ = this.documentService.documentAccessData$;
  }

  ngOnDestroy() {
    this.notificationsService.showComponent$.next(null);
  }
}
