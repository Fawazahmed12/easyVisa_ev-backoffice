import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { map } from 'rxjs/operators';

import { Note } from '../../models/note.model';
import { BlankForm } from '../../models/forms-sheets.model';


@Injectable()
export class FormsSheetsRequestService {

  constructor(
    private httpClient: HttpClient
  ) {
  }

  formsSheetsGetRequest(data) {
    return this.httpClient.get<Note[]>(`/document/uscis`, {params: data});
  }

  printFormGetRequest(params) {
    return this.httpClient.get<any>(
      `/document/uscis/form`, {
        params,
        observe: 'response',
        responseType: 'blob' as 'json',
      }).pipe(
      map((resp: any) => ({
          file: resp.body,
          fileName: resp.headers.get('X-file-name')
        }))
    );
  }

  downloadFormsGetRequest(params) {
    return this.httpClient.get<any>(
      `/document/uscis/forms`, {
        params,
        observe: 'response',
        responseType: 'blob' as 'json',
        reportProgress: true
      }).pipe(
      map((resp: any) => ({
          file: resp.body,
          fileName: resp.headers.get('X-file-name')
        }))
    );
  }

  blanksGetRequest(data) {
    return this.httpClient.get<BlankForm[]>(`/document/forms`, {params: data});
  }

  downloadBlanksGetRequest(params) {
    return this.httpClient.get<any>(`/document/uscis/blankforms`, {
      params,
      observe: 'response',
      responseType: 'blob' as 'json',
    }).pipe(
      map((resp: any) => ({
          file: resp.body,
          fileName: resp.headers.get('X-file-name')
        }))
    );
  }

  printBlankGetRequest(params) {
    return this.httpClient.get<any>(`/document/uscis/blankform`, {
      params,
      observe: 'response',
      responseType: 'blob' as 'json',
    }).pipe(
      map((resp: any) => ({
          file: resp.body,
          fileName: resp.headers.get('X-file-name')
        }))
    );
  }
}


