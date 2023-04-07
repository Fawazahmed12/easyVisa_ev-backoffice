import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { Warning } from '../../models/warning.model';
import { Disposition } from '../../models/dispositions.model';

import { PackageApplicant } from '../../../core/models/package/package-applicant.model';
import { Package } from '../../../core/models/package/package.model';
import { FileInfo } from '../../../core/models/file-info.model';


@Injectable()
export class TaskQueueModuleRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  applicantRequest(searchParam) {
    return this.httpClient.get<PackageApplicant>(`/applicants/find`, {params: searchParam});
  }

  packagesRequest(params?): Observable<Package[]> {
    return this.httpClient.get<Package[]>(`/packages/find`, {params});
  }

  packageRequest(id) {
    return this.httpClient.get<Package>(`/packages/${id}`);
  }

  packagePostRequest(data) {
    return this.httpClient.post<Observable<Package>>(`/packages`, data);
  }

  packagePatchRequest(data) {
    return this.httpClient.put<Observable<{ package: Package; message: string }>>(
      `/packages/${data.id}`,
      {
        applicants: data.applicants,
        id: data.id,
        representativeId: data.representativeId,
        owed: data?.owed,
      },
      {params: data.params ? data.params : null}
    );
  }

  packageWelcomeEmailPostRequest(packageId) {
    return this.httpClient.post<Observable<{ message: string }>>(`/packages/${packageId}/send-welcome-email`, {id: packageId});
  }

  applicantInvitePostRequest(params) {
    return this.httpClient.post<Observable<{ message: string }>>(
      `/packages/${params.packageId}/send-applicant-invite`,
      {applicantId: params.applicantId},
    );
  }

  retainerAgreementPostRequest(data) {
    return this.httpClient.post<Observable<FileInfo>>(`packages/${data.id}/retainer`, data.file);
  }

  retainerAgreementDeleteRequest(packageId) {
    return this.httpClient.delete(`packages/${packageId}/retainer`);
  }

  changePackageStatusPostRequest(data) {
    const newStatus = {
      newStatus: data.newStatus
    };
    return this.httpClient.post(`packages/${data.id}/change-status`, newStatus);
  }

  changePackageOwedPatchRequest(data) {
    const owed = {
      owed: data.owed
    };
    return this.httpClient.patch(`packages/${data.id}/owed-amount`, owed);
  };

  deleteLeadPackagesRequest(params) {
    return this.httpClient.delete('packages', {params});
  }

  deleteSelectedLeadPackagesRequest(params) {
    return this.httpClient.delete('packages/leads', {params});
  }

  deleteSelectedTransferredPackagesRequest(params) {
    return this.httpClient.delete('packages/transferred', {params});
  }

  warningsGetRequest(params): Observable<{ body: Warning[]; xTotalCount: string }> {
    return this.httpClient.get<Warning[]>(
      `warnings`,
      {params, observe: 'response'}
    ).pipe(
      map(({body, headers}) => {
        const xTotalCount = headers.get('x-total-count');
        return {body, xTotalCount};
      })
    );
  }

  warningsDeleteRequest(params) {
    return this.httpClient.delete(`warnings`, {params: {ids: params.ids}}).pipe(
      map((res) => ({
          params: params.query,
          ...res
        })
      ));
  }

  warningPutRequest(warning) {
    return this.httpClient.put<Observable<Warning>>(`/warnings/${warning.id}`, warning);
  }

  feesBillPostRequest(data) {
    return this.httpClient.post(
      `packages/${data.packageId}/send-bill`,
      {
        charges: data.charges,
        email: data.email,
      });
  }

  dispositionsGetRequest(params): Observable<{ body: Disposition[]; xTotalCount: string }> {
    return this.httpClient.get<Disposition[]>(
      `dispositions`,
      {params, observe: 'response'}
    ).pipe(
      map(({body, headers}) => {
        const xTotalCount = headers.get('x-total-count');
        return {body, xTotalCount};
      })
    );
  }

  dispositionDataGetRequest(id): Observable<any> {
    return this.httpClient.get<any>(`dispositions/${id}`, {
      observe: 'response',
      responseType: 'blob' as 'json',
    }).pipe(
      map((resp: any) => {
        const fileName = resp.headers.get('X-file-name');
        const _id = resp.headers.get('id');
        const approved = resp.headers.get('X-disposition-approved');
        return {
          file: resp.body,
          fileName,
          id: _id,
          approved,
        };
      })
    );
  }

  dispositionPutRequest(data): Observable<any> {
    return this.httpClient.put<any>(`dispositions/${data.id}`, data.payload, {observe: 'response'}).pipe(
      map((res) => {
        const totalDispositions = res.headers.get('X-total-count');
        return {
          ...res.body,
          totalDispositions,
          actionType: data.actionType
        };
      })
    );
  }
}
