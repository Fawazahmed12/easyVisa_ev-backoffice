import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { Alert } from '../../../task-queue/models/alert.model';
import { map } from 'rxjs/operators';
import { Disposition } from '../../../task-queue/models/dispositions.model';

@Injectable()
export class AlertsRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  alertsRequest(params): Observable<{ body: Alert[]; xTotalCount: string }> {
    return this.httpClient.get<Alert[]>(
      `alerts`,
      {params, observe: 'response'}
      ).pipe(
      map(({body, headers}) => {
        const xTotalCount = headers.get('x-total-count');
        return {body, xTotalCount};
      })
    );
  }

  alertPutRequest(alert) {
    return this.httpClient.put<Observable<Alert>>(`/alerts/${alert.id}`, alert);
  }

  alertsDeleteRequest(params) {
    return this.httpClient.delete(`alerts`, {params: {ids: params.ids}}).pipe(
      map((res) => ({
        params: params.query,
        ...res
      })
    ));
  }

  sendAlertRequest(data) {
    return this.httpClient.post(`admin/alerts`, data);
  }
}
