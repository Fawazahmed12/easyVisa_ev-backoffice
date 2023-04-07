import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanActivateChild, Router, RouterStateSnapshot } from '@angular/router';

import { AuthService } from '../services';

@Injectable()
export class StoreUrlGuardService implements CanActivate, CanActivateChild {

  // This guard captures the current URL and save it into the store
  // which will be useful to redirect after login, from timeout
  // It will always allows true
  constructor(
    private router: Router,
    private authService: AuthService,
  ) {
  }

  canActivateChild(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.authService.setRedirectUrl(state.url);
    return true;
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.authService.setRedirectUrl(state.url);
    return true;
  }
}
