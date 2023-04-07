import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { map } from 'rxjs/operators';

import { Attorney, AttorneyMenu } from '../../models/attorney.model';
import { FeeSchedule } from '../../models/fee-schedule.model';

@Injectable()
export class RepresentativesRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  representativesGetRequest(id) {
    return this.httpClient.get<Attorney[]>(`/organizations/${id}/representatives`);
  }

  representativesMenuGetRequest(id) {
    return this.httpClient.get<AttorneyMenu[]>(`/organizations/${id}/representatives`, {params: {view: 'menu'}});
  }

  attorneysValidatePostRequest(data: { email: string; easyVisaId: string }) {
    return this.httpClient.post<{ id: number }>(`/attorneys/validate`, data);
  }

  attorneyPatchRequest(data) {
    return this.httpClient.patch(`/attorneys/${data.id}`, data);
  }

  feeScheduleGetRequest(id) {
    return this.httpClient.get<FeeSchedule[]>(`/attorneys/${id}/fee-schedule`).pipe(
      map(response => ({response, id}))
    );
  }
}
