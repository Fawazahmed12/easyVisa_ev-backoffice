import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot
} from '@angular/router';

import { filter, map, switchMapTo, take, withLatestFrom } from 'rxjs/operators';

import { AuthService, UserService } from '../services';
import { User } from '../models/user.model';
import { RegistrationStatus } from '../models/registration-status.enum';
import { Role } from '../models/role.enum';


@Injectable()
export class ConvertToAttorneyGuardService implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.authService.isLoggedIn$.pipe(
      filter((isLoggedIn) => isLoggedIn !== null),
      switchMapTo(this.userService.currentUser$),
      withLatestFrom(this.userService.registrationRepresentativeType$),
      map(([user, representativeType]: [User, string]) => {
        if (!user) {
          this.router.navigate(['auth', 'login']);
        } else if (
          user.roles.some((role) => role === Role.ROLE_EMPLOYEE)
          && representativeType === RegistrationStatus.CONVERT_TO_ATTORNEY) {
          return true;
        } else if (representativeType !== RegistrationStatus.CONVERT_TO_ATTORNEY) {
          this.router.navigate(['account', 'profile']);
        } else {
          return true;
        }
      }),
      take(1),
    );
  }
}
