import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { switchMap, take, withLatestFrom } from 'rxjs/operators';
import { of } from 'rxjs';

import { PackagesService, UserService } from '../services';
import { PackageStatus } from '../models/package/package-status.enum';


@Injectable()
export class MyPackagesResolverService implements Resolve<any> {

  constructor(
    private packagesService: PackagesService,
    private userService: UserService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.userService.currentUser$.pipe(
      withLatestFrom(this.packagesService.packages$),
      switchMap(([user, packages]) => packages.length ? of(packages)
        : this.packagesService.getPackages(
          {
            search: user.profile.easyVisaId,
            status: [
              PackageStatus.BLOCKED,
              PackageStatus.OPEN,
              PackageStatus.LEAD,
              PackageStatus.CLOSED,
              PackageStatus.TRANSFERRED,
              PackageStatus.DELETED
            ]
          })),
      take(1)
    );
  }
}
