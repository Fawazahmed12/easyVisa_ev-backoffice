import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { EMPTY } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';

import { PackagesService } from '../../core/services';
import { DocumentsService } from '../services';

@Injectable()
export class SentDocumentsResolverService implements Resolve<any> {

  constructor(
    private httpClient: HttpClient,
    private documentsService: DocumentsService,
    private packagesService: PackagesService
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.packagesService.activePackageId$
      .pipe(
        filter((activePackageId) => !!activePackageId),
        switchMap((activePackageId) => this.documentsService.getApplicantSentDocuments(activePackageId)),
        take(1),
        catchError(() => EMPTY)
      );
  }
}
