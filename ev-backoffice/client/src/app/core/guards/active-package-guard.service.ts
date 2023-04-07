import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, } from '@angular/router';

import { Observable } from 'rxjs';
import { filter, switchMap, take } from 'rxjs/operators';
import { of } from 'rxjs';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { PackagesService, UserService } from '../services';
import { PackageStatus } from '../models/package/package-status.enum';
import { NoPackageSelectModalComponent } from '../modals/no-package-select-modal/no-package-select-modal.component';
import { EMPTY } from 'rxjs';

@Injectable()
export class ActivePackageGuardService implements CanActivate {

  constructor(
    private router: Router,
    private userService: UserService,
    private packagesService: PackagesService,
    private ngbModal: NgbModal
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    return this.packagesService.getActivePackageId().pipe(
      switchMap((hasActivePackageId) => {
        if (!hasActivePackageId) {
          this.ngbModal.open(NoPackageSelectModalComponent, { centered: true });
          return EMPTY;
        } else {
          return this.packagesService.activePackage$.pipe(
            filter((activePackage) => !!activePackage),
            switchMap((activePackage) => {
              if (activePackage[ 'status' ] === PackageStatus.LEAD) {
                return of(false);
              } else {
                return of(true);
              }
            })
          );
        }
      }),
      take(1),
    );
  }
}

