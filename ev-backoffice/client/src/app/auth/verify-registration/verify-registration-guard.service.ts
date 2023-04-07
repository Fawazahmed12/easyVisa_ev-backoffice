import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';

@Injectable()
export class VerifyRegistrationGuardService implements CanActivate {

  constructor(
    private router: Router
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    if (route.queryParams[ 'token' ]) {
      return true;
    }
    this.router.navigate(['auth', 'attorney-sign-up']);
    return false;
  }
}
