import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';

import { EMPTY } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';

import { PackagesService } from '../../core/services';

import { PrintFormsSheetsService } from '../services/print-forms-sheets.service';

@Injectable()
export class PrintFormsSheetsResolverService implements Resolve<any> {

  constructor(
    private httpClient: HttpClient,
    private packagesService: PackagesService,
    private router: Router,
    private printFormsSheetsService: PrintFormsSheetsService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.packagesService.activePackageId$
      .pipe(
        filter((activePackageId) => !!activePackageId),
        switchMap((activePackageId) => this.printFormsSheetsService.getFormsSheets({ packageId: activePackageId })),
        take(1),
        catchError(() => EMPTY)
      );
  }
}
