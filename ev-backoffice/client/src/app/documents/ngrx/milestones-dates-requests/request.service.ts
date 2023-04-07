import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Note } from '../../models/note.model';


@Injectable()
export class MilestoneDatesRequestService {

  constructor(
    private httpClient: HttpClient
  ) {
  }

  milestoneDatesGetRequest(data) {
    return this.httpClient.get<Note[]>(`/document/milestone`, {params: data});
  }

  milestoneDatePostRequest(data) {
    return this.httpClient.post<Note>(`/document/milestone`, {...data});
  }
}


