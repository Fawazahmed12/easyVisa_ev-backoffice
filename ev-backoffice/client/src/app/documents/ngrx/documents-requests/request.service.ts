import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import {
  DocumentAccessState,
  DocumentAttachmentListState,
  DocumentPortalAccessState,
  RequiredApplicantDocumentModel
} from '../../models/documents.model';
import { ApplicantSentDocuments } from '../../models/sent-document.model';
import { ApplicantReceivedDocuments } from '../../models/received-document.model';


@Injectable()
export class DocumentsRequestService {

  constructor(private httpClient: HttpClient) {
  }

  documentAccessGetRequest(packageId: string): Observable<DocumentAccessState> {
    return this.httpClient.get<DocumentAccessState>(`/document/access/package/${packageId}`);
  }

  documentPortalAccessGetRequest(packageId: string): Observable<DocumentPortalAccessState> {
    return this.httpClient.get<DocumentPortalAccessState>(`/questionnaire/access/package/${packageId}`);
  }

  requiredDocumentsGetRequest(packageId: string): Observable<RequiredApplicantDocumentModel[]> {
    return this.httpClient.get<RequiredApplicantDocumentModel[]>(`/document/package/${packageId}/requireddocuments`);
  }

  uploadDocumentAttachmentPostRequest(data) {
    return this.httpClient.post<DocumentAttachmentListState>(`/document/attachment`, data);
  }

  downloadDocumentAttachmentsGetRequest(params) {
    return this.httpClient.get<any>(`/document/attachments`, {
      params,
      observe: 'response',
      responseType: 'blob' as 'json'
    }).pipe(
      map(resp => ({
          file: resp.body,
          fileName: resp.headers.get('X-file-name')
        }))
    );
  }

  documentAttachmentsDeleteRequest(params) {
    return this.httpClient.delete<DocumentAttachmentListState>(`/document/attachments`, { params });
  }

  previewDocumentAttachment(params) {
    return this.httpClient.get<any>(`/document/attachment`, {
      params,
      observe: 'response',
      responseType: 'blob' as 'json'
    }).pipe(
      map(resp => ({
          file: resp.body,
          fileName: resp.headers.get('X-file-name'),
          fileType:resp.headers.get('content-type').replace(';charset=utf-8','')
        }))
    );
  }


  fetchThumbnailAttachment(params) {
    return this.httpClient.get<any>(`/document/attachment/thumbnail`, {
      params,
      observe: 'response',
      responseType: 'blob' as 'json'
    }).pipe(
      map(resp => ({
          file: resp.body,
          fileName: resp.headers.get('X-file-name')
        }))
    );
  }

  sentDocumentsGetRequest(packageId: string): Observable<ApplicantSentDocuments[]> {
    return this.httpClient.get<ApplicantSentDocuments[]>(`/document/package/${packageId}/sentdocuments`);
  }

  documentActionDatePostRequest(data) {
    return this.httpClient.post<any>(`/document/actiondate`, data);
  }

  receivedDocumentsGetRequest(packageId: string): Observable<ApplicantReceivedDocuments[]> {
    return this.httpClient.get<ApplicantReceivedDocuments[]>(`/document/package/${packageId}/receiveddocuments`);
  }

  documentApprovalPatchRequest(data) {
    return this.httpClient.patch<any>(`/document/approve`, data);
  }

  downloadAllDocumentAttachmentsGetRequest(params) {
    return this.httpClient.get<any>(`/document/attachments/all`, {
      params,
      observe: 'response',
      responseType: 'blob' as 'json'
    }).pipe(
      map(resp => ({
          file: resp.body,
          fileName: resp.headers.get('X-file-name')
        }))
    );
  }
}
