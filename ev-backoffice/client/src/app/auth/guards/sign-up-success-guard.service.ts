import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';

import { map, take } from 'rxjs/operators';

import { SignUpService } from '../services';

@Injectable()
export class SignUpSuccessGuardService implements CanActivate {

  constructor(
    private router: Router,
    private signUpService: SignUpService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.signUpService.attorneySignUpInfo$.pipe(
      map((info) => {
        if (info) {
          return true;
        } else {
          this.router.navigate(['auth', 'attorney-sign-up']);
        }
      }),
      take(1),
    );
  }

}
