import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Note } from '../../models/note.model';


@Injectable()
export class NotesRequestService {

  constructor(
    private httpClient: HttpClient
  ) {
  }

  notesGetRequest(data) {
    return this.httpClient.get<Note[]>(`/document/notes`, {params: data});
  }

  notePostRequest(data) {
    return this.httpClient.post<Note>(`/document/notes`, data);
  }

  noteDeleteRequest(data) {
    return this.httpClient.delete<any>(`/document/notes`, {params: data});
  }
}


