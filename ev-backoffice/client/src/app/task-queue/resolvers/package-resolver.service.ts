import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';

import { catchError, filter, take } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

import { PackagesService } from '../../core/services';


@Injectable()
export class PackageResolverService implements Resolve<any> {

  constructor(
    private packagesService: PackagesService,
    private router: Router,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const packageId = route.params.id;
    return this.packagesService.getPackage(packageId).pipe(
      filter((res) => !!res),
      catchError((err) => {
        this.router.navigate(['task-queue', 'package', 'create']);
        return EMPTY;
      }),
      take(1),
    );
  }
}
