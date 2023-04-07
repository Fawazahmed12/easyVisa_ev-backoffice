import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';

import { EMPTY } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';

import { PackagesService } from '../../core/services';
import { DocumentsService } from '../services';

@Injectable()
export class DocumentsAccessResolverService implements Resolve<any> {

  constructor(
    private httpClient: HttpClient,
    private documentsService: DocumentsService,
    private packagesService: PackagesService,
    private router: Router
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.packagesService.activePackageId$
      .pipe(
        filter((activePackageId) => !!activePackageId),
        switchMap(activePackageId => this.documentsService.getDocumentAccessRequest(activePackageId)),
        take(1),
        catchError((err) => {
          this.router.navigate([ 'home' ]);
          return EMPTY;
        })
      );
  }
}
