import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';


import { catchError, filter, switchMap, take, tap } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

import { ModalService, OrganizationService } from '../../../core/services';

import { PermissionsService } from '../permissions.service';


@Injectable()
export class EditUserResolverService implements Resolve<any> {

  constructor(
    private permissionsService: PermissionsService,
    private organizationService: OrganizationService,
    private router: Router,
    private modalService: ModalService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const employeeId = route.params.id;
    return this.organizationService.activeOrganizationId$.pipe(
      filter((organizationId) => !!organizationId),
      switchMap((organizationId) => this.permissionsService.getPermission(
        { employeeId, organizationId }).pipe(
        catchError((error: HttpErrorResponse) =>
          this.modalService.showErrorModal(error.error.errors).pipe(
            catchError(() => EMPTY),
            tap(() => this.router.navigate(['account', 'permission'])),
          )
        ),
      )),
      take(1));
  }
}
