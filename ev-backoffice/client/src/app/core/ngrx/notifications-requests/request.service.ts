import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { TaskQueueCounts } from '../../models/task-queue-counts.model';

@Injectable()
export class NotificationsRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  taskQueueCountsGetRequest(data): Observable<TaskQueueCounts> {
    return this.httpClient.get<TaskQueueCounts>(`unread/count`, {params: data});
  }
}
