import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot
} from '@angular/router';

import { filter, map, switchMapTo, take, tap, withLatestFrom } from 'rxjs/operators';

import { AuthService, UserService } from '../services';
import { User } from '../models/user.model';
import { RegistrationStatus } from '../models/registration-status.enum';
import { Attorney } from '../models/attorney.model';
import { Role } from '../models/role.enum';
import { AttorneyType } from '../models/attorney-type.enum';
import { RepresentativeType } from '../models/representativeType.enum';

@Injectable()
export class RegistrationFinishGuardService implements CanActivate {


  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.authService.isLoggedIn$.pipe(
      tap(() => {
        if (state.url !== '/home') {
          this.authService.setRedirectUrl(state.url);
        }
      }),
      filter((isLoggedIn) => isLoggedIn !== null),
      switchMapTo(this.userService.currentUser$),
      map((user: User) => {
        if (!user) {
          this.router.navigate(['auth', 'login']);
        } else if (user.roles.some((role) => role === Role.ROLE_ATTORNEY)) {
          return this.redirectToAuth(user.profile as Attorney, route.data.step);
        } else {
          return true;
        }
      }),
      take(1),
    );
  }

  redirectToAuth(attorney: Attorney, step?) {
    if (
      attorney.registrationStatus === RegistrationStatus.EMAIL_VERIFIED && step !== 2) {
      this.router.navigate(['auth', 'standard-ev-charges']);
    } else if (
      attorney.registrationStatus === RegistrationStatus.CONTACT_INFO_UPDATED
      && step !== 3
      && step !== 2
      && step !== 1
    ) {
      this.router.navigate(['auth', 'pay-sign-up-fee']);
    } else {
      return true;
    }
  }
}
