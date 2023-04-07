import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { USCISFormModel } from './pdf-print-testing.model';


@Injectable()
export class PdfPrintTestingService {

  constructor(private httpClient: HttpClient) {
  }

  fetchQuestionnaireForms(packageId) {
    return this.httpClient.get<USCISFormModel[]>(
      `/package/${packageId}/questionnaireforms`
    );
  }

  printPdfForm(params) {
    const httpOptions = {
      Accept: 'application/json',
      responseType: 'arraybuffer' as 'json',
      params
    };
    return this.httpClient.get<any>(`/pdf`, httpOptions);
  }
}
