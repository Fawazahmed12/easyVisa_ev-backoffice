import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';

import { FeeDetails } from '../../models/fee-details.model';
import { GovernmentFee } from '../../models/government-fee.model';
import { BenefitCategoryModel } from '../../models/benefits.model';

@Injectable()
export class ConfigDataRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  feeDetailsPost(feeDetails: FeeDetails) {
    return this.httpClient.post('/admin-config', feeDetails);
  }

  configDataGetRequest(): Observable<FeeDetails> {
    return this.httpClient.get<FeeDetails>(`/public/admin-config`);
  }

  governmentFeeGetRequest(): Observable<GovernmentFee> {
    return this.httpClient.get<GovernmentFee>(`/public/admin-config/government-fees`);
  }

  benefitsGetRequest(): Observable<BenefitCategoryModel> {
    return this.httpClient.get<BenefitCategoryModel>('/benefits');
  }
}
