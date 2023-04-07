import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';

import { filter, switchMapTo, take, tap } from 'rxjs/operators';

import { AuthService, OrganizationService } from '../services';

@Injectable()
export class PositionGuardService implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService,
    private organizationService: OrganizationService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.authService.isLoggedIn$.pipe(
      filter((isLoggedIn) => isLoggedIn !== null),
      switchMapTo(this.organizationService.hasAccessByPosition(route.data.positions)),
      tap((isPosition: boolean) => {
        if (!isPosition) {
          this.router.navigate(['/account']);
        }
      }),
      take(1),
    );
  }

}
