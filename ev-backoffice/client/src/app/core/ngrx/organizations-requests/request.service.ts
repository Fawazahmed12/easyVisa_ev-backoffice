import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';

import { Observable } from 'rxjs';

import { Organization } from '../../models/organization.model';
import { EmployeeStatusValues } from '../../../account/permissions/models/employee-status.enum';

@Injectable()
export class OrganizationsRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  menuOrganizationsGetRequest(): Observable<HttpResponse<Organization[]>> {
    return this.httpClient.get<HttpResponse<Organization[]>>(`/organizations`, {params: {status: EmployeeStatusValues.ACTIVE}});
  }

  affiliatedOrganizationsGetRequest(): Observable<HttpResponse<Organization[]>> {
    return this.httpClient.get<HttpResponse<Organization[]>>(`/organizations`);
  }
}
