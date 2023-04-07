import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { delay, map } from 'rxjs/operators';

import { UscisEditionDatesModel } from '../../models/uscis-edition-dates.model';
import { Alert } from '../../../task-queue/models/alert.model';

@Injectable()
export class UscisEditionDatesRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  uscisEditionDatesGetRequest(params): Observable<UscisEditionDatesModel[]> {
    return this.httpClient.get<UscisEditionDatesModel[]>(`/attorneys/uscis-edition-dates`,{params});
  }


  uscisEditionDatesPutRequest(uscisDates): Observable<UscisEditionDatesModel[]> {
    return this.httpClient.put<UscisEditionDatesModel[]>(`/attorneys/uscis-edition-dates`, uscisDates);
  }

}
