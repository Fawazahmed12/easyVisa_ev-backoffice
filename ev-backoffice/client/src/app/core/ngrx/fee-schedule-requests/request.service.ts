import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { FeeSchedule } from '../../models/fee-schedule.model';

@Injectable()
export class FeeScheduleRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  feeScheduleSettingsGetRequest(): Observable<FeeSchedule[]> {
    return this.httpClient.get<FeeSchedule[]>(`public/admin-config/fee-schedule`);
  }

  feeScheduleSettingsPostRequest(data): Observable<FeeSchedule[]> {
    return this.httpClient.post<FeeSchedule[]>(`admin-config/fee-schedule`, {feeSchedule: data});
  }
}
