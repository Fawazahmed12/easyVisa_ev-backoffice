import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';

import { catchError, filter, map, take } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

import { ModalService, PackagesService } from '../../core/services';


@Injectable()
export class ClientsPackageResolverService implements Resolve<any> {

  constructor(
    private packagesService: PackagesService,
    private modalService: ModalService,
    private router: Router,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const packageId = route.params.id;
    return this.packagesService.getPackage(packageId).pipe(
      filter((res) => !!res),
      catchError((error) =>
        this.modalService.showErrorModal(error.error.errors || [error.error]).pipe(
          catchError(() => EMPTY),
          map((params) => this.router.navigate(['task-queue', 'clients']))
        )),
      take(1),
    );
  }
}
