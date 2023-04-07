import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';

import { FeeDetails } from '../../../../core/models/fee-details.model';
import { GovernmentFee } from '../../../../core/models/government-fee.model';
import { RankingData } from '../../../models/ranking-data.model';
import { Job } from '../../../models/site-jobs';

@Injectable()
export class DashboardSettingsRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  governmentFeePost(governmentFee: GovernmentFee) {
    return this.httpClient.post(`/admin-config/government-fees`, governmentFee);
  }

  rankingDataGet() {
    return this.httpClient.get<Observable<RankingData>>(`ranking-data`);
  }

  rankingDataPut(data) {
    return this.httpClient.post<Observable<RankingData>>(`ranking-data`, data);
  }

  representativesCountGet() {
    return this.httpClient.get<Observable<any>>(`representatives-count`);
  }

  batchJobsGet() {
    return this.httpClient.get<Observable<any>>(`/admin-config/batch-jobs`);
  }

  batchJobsPatch(job: Job) {
    return this.httpClient.patch<Observable<any>>(`/admin-config/batch-jobs`, job);
  }
}
