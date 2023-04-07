import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { switchMap, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { PackagesService } from '../../../core/services';

import { ProgressStatusService } from '../progress-status.service';


@Injectable()
export class DocumentStatusResolverService implements Resolve<any> {

  constructor(
    private packagesService: PackagesService,
    private progressStatusService: ProgressStatusService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.packagesService.activePackageId$.pipe(
      switchMap((packageId) => {
        if (packageId) {
          return this.progressStatusService.getDocumentProgress(packageId);
        } else {
          return of(true);
        }
      }),
      take(1)
    );
  }
}
