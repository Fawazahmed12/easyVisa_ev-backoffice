import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { User } from '../../models/user.model';

@Injectable()
export class UserRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  userGetRequest(): Observable<User> {
    return this.httpClient.get<User>(`/users/me`);
  }

  userDeleteRequest() {
    return this.httpClient.delete<User>(`/users/me`);
  }

  changeMembershipRequest(data: {payload: boolean; type: string}) {
    return this.httpClient.patch<User>(`/users/me`, {activeMembership: data.payload}).pipe(
      map((res) => ({
          user: res,
          actionType: data.type
        }))
    );
  }

  userIdByEVIdGetRequest(data: string): Observable<number> {
    return this.httpClient.get<number>(`/users/ev-id/${data}/id`);
  }

  convertToAttorneyPostRequest(data) {
    return this.httpClient.post(`employees/convert-to-attorney`, data);
  }
}
