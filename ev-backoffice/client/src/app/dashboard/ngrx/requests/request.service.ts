import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Article } from '../../articles/models/article.model';
import { MarketingDetails } from '../../models/marketing-details.model';
import { FinancialDetails } from '../../models/financial-details.model';
import { RawHttpClient } from '../../../core/raw-http-client';
import { map } from 'rxjs/operators';
import { Email } from '../../../core/models/email.model';


@Injectable()
export class DashboardRequestService {

  constructor(
    private httpClient: HttpClient,
    private rawHttpClient: RawHttpClient,
  ) {
  }

  articlesGet(data): Observable<HttpResponse<Article[]>> {
    return this.httpClient.get<Article[]>(
      `/public/attorneys/articles`,
      {params: data, observe: 'response'}
      ).pipe(
      map((res) => {
        const xTotalCount = res.headers.get('x-total-count');
        return res;
      })
    );
  }

  articleCategoriesGet() {
    return this.rawHttpClient.get<any>(`https://marketing.easyvisa.com/categories/list`);
  }

  articlePost(data: Article) {
    return this.httpClient.post<Observable<Article>>(`articles`, data);
  }

  marketingDetailsGet(data) {
    return this.httpClient.get<MarketingDetails>(
      `attorneys/${data.representativeId}/marketing`,
      {params: {organizationId: data.organizationId}});
  }

  financialDetailsGet(data) {
    return this.httpClient.get<FinancialDetails>(
      `attorneys/${data.representativeId}/financial`,
      {params: {organizationId: data.organizationId}});
  }

  inviteColleaguesPost(email) {
    return this.httpClient.post<Observable<Email>>(`attorneys/invite-colleagues`, email);
  }

}
