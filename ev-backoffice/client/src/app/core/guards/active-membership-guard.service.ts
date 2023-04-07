import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';

import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

import { UserService } from '../services';


@Injectable()
export class ActiveMembershipGuardService implements CanActivate {

  constructor(
    private router: Router,
    private userService: UserService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    return this.userService.activeMembership$.pipe(
      map((activeMembership) => {
          if (!activeMembership) {
            this.router.navigate(['account', 'profile']);
          }
          return activeMembership;
        }),
      take(1),
    );
  }
}

