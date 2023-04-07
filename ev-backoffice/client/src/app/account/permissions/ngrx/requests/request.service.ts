import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { OrganizationEmployee } from '../../models/organization-employee.model';
import { of } from 'rxjs';
import { delay, mapTo } from 'rxjs/operators';


@Injectable()
export class PermissionsModuleRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  inviteMemberPut(data) {
    return this.httpClient.put(
      `/organizations/${data.activeOrganizationId}/invitation`,
      {...data.formGroup}
      );
  }

  verifyMemberPost(data) {
    return this.httpClient.post(
      `/organizations/${data.organizationId}/validate-invite-member`,
      {
        email: data.email,
        easyVisaId: data.easyVisaId
      });
  }

  inviteDeleteRequest(data) {
    return this.httpClient.delete<any>(`/organizations/${data.organizationId}/employee/${data.employeeId}`).pipe(mapTo(data.employeeId));
  }

  permissionsGetRequest(data) {
    return this.httpClient.get<OrganizationEmployee[]>(`/organizations/${data.organizationId}/employee-permissions`, {params: data.params});
  }

  permissionGetRequest(data) {
    return this.httpClient.get(`/organizations/${data.organizationId}/employees/${data.employeeId}`);
  }

  createEmployeePost(data) {
    return this.httpClient.post(`/organizations/${data.organizationId}/employees`, {...data.organizationEmployee});
  }

  updateEmployeePut(data) {
    return this.httpClient.put(`/organizations/${data.organizationId}/employees/${data.employeeId}`, { ...data.organizationEmployee});
  }
}
