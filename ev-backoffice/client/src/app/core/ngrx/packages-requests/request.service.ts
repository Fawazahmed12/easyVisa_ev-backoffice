import { Injectable } from '@angular/core';
import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';

import { map } from 'rxjs/operators';

import { GetPackages } from '../packages/packages.actions';

import { Attorney } from '../../models/attorney.model';
import { Package } from '../../models/package/package.model';

@Injectable()
export class PackagesRequestService {

  constructor(
    private httpClient: HttpClient,
  ) {
  }

  activePackageGetRequest(id) {
    return this.httpClient.get<Attorney[]>(`/packages/${id}`);
  }

  packagesRequest({payload, type}: GetPackages) {
    const params = new HttpParams({fromObject: payload.params});
    return this.httpClient.get<HttpResponse<Package[]>>(`/packages/find`, {params, observe: 'response'}).pipe(
      map((res) => {
        const xTotalCount = res.headers.get('x-total-count');
        return {
          ...res,
          actionType: type,
          isShowModal: payload.isShowModal,
          isEditPackagePage: payload.isEditPackagePage,
        };
      })
    );
  }

  packagesTransferPostRequest(data) {
    return this.httpClient.post(`packages/transfer`, data);
  }

  packagesTransferByApplicantPostRequest(data) {
    return this.httpClient.post(`applicants/package/transfer`, data);
  }
}
