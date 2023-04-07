import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Email } from '../../models/email.model';
import { PreviewedEmail } from '../../models/previewed-email.model';

@Injectable()
export class EmailsRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  emailPostRequest(email) {
    return this.httpClient.post<Observable<Email>>(`/email`, email);
  }

  previewUnsavedEmailPostRequest(data) {
    return this.httpClient.post<Observable<PreviewedEmail>>(`email-templates/${data.templateType}/preview`, data);
  }

  emailByIdGetRequest(emailId) {
    return this.httpClient.get<Observable<Email>>(`/email/${emailId}`);
  }

  emailPutRequest(email) {
    return this.httpClient.put<Observable<Email>>(`/email/${email.id}`, email);
  }

}
