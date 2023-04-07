import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { ProgressStatus } from '../../models/progress-status.model';


@Injectable()
export class ProgressStatusesModuleRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  questionnaireProgressGetRequest(packageId) {
    return this.httpClient.get<ProgressStatus[]>(`/questionnaire/progress/package/${packageId}`);
  }

  documentProgressGetRequest(packageId) {
    return this.httpClient.get<ProgressStatus[]>(`/document/progress/package/${packageId}`);
  }
}
