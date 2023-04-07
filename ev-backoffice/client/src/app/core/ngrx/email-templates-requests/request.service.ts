import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';

import { EmailTemplate } from '../../models/email-template.model';
import { mockEmailTemplate } from '../../../account/email-templates/mocks/email-template.mock';

@Injectable()
export class EmailTemplatesRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  emailTemplateGetRequest(params): Observable<EmailTemplate> {
    const reqParams = {
      representativeId: params.representativeId,
      defaultTemplate: params.defaultTemplate,
    };
    return this.httpClient.get<EmailTemplate>(`/email-templates/${params.type}`, {params: reqParams}).pipe(
      map((res) => params.defaultTemplate ? {...res, isDefault: true} : res)
    );
  }

  emailTemplatesGetRequest(params): Observable<EmailTemplate[]> {
    return this.httpClient.get<EmailTemplate[]>(`/email-templates`, {params});
  }

  emailTemplatePutRequest(emailTemplate): Observable<EmailTemplate> {
     return this.httpClient.put<EmailTemplate>(`/email-templates`, emailTemplate).pipe(
       map((res) => ({...res, isDefault: false}))
     );
  }

  defaultEmailTemplateGetRequest() {
    // TODO: change when backend would be ready
    // return this.httpClient.get<EmailTemplate>(`/email-template?templateType=${emailTemplateType}`);
    return of(
      mockEmailTemplate,
    ).pipe(
      delay(1000),
    );
  }

  emailTemplateVariablesGetRequest(params): Observable<any> {
    return this.httpClient.get<EmailTemplate[]>(`/email-template-variables`, {params});
  }

}
