import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { mapTo } from 'rxjs/operators';


@Injectable()
export class AlertHandlingRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  alertReplyPutRequest(data: { id: number; accept: boolean }) {
    return this.httpClient.put(`/alerts/${data.id}/reply`, data).pipe(mapTo({updateOrgMenu: data.accept}));
  }
}
