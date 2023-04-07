import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';

import { filter, map, take, withLatestFrom } from 'rxjs/operators';

import { OrganizationService, UserService } from '../services';

@Injectable()
export class RoleAndAdminGuardService implements CanActivate {

  constructor(
    private organizationService: OrganizationService,
    private router: Router,
    private userService: UserService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.activeOrganization$.pipe(
      filter((activeOrganization) => !!activeOrganization),
      withLatestFrom(this.userService.hasAccess(route.data.roles)),
      map(([activeOrganization, hasAccess]) => {
        if (!(hasAccess || activeOrganization.isAdmin)) {
          this.router.navigate(['account', 'profile']);
        }
        return hasAccess || activeOrganization.isAdmin;
      }),
      take(1)
    );
  }

}
