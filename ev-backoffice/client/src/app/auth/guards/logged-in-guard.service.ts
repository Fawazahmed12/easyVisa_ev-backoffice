import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';

import { filter, map, take } from 'rxjs/operators';

import { AuthService } from '../../core/services';

@Injectable()
export class LoggedInGuardService implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.authService.isLoggedIn$.pipe(
      filter((isLoggedIn) => isLoggedIn !== null),
      map((login) => {
        if (!login) {
          return true;
        } else {
          this.router.navigate(['index']);
        }
      }),
      take(1),
    );
  }

}
