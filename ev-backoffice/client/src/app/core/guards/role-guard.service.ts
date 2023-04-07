import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';

import { filter, switchMapTo, take, tap } from 'rxjs/operators';

import { AuthService, UserService } from '../services';

@Injectable()
export class RoleGuardService implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.authService.isLoggedIn$.pipe(
      filter((isLoggedIn) => isLoggedIn !== null),
      switchMapTo(this.userService.hasAccess(route.data.roles)),
      tap((isUser: boolean) => {
        if (!isUser) {
          this.router.navigate(['/index']);
        }
      }),
      take(1),
     );
  }

}
