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
export class UnpaidGuardService implements CanActivate {

  constructor(
    private router: Router,
    private userService: UserService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    return this.userService.paidStatus$.pipe(
      map((paidStatus) => {
        if (!paidStatus) {
          this.router.navigate(['account', 'payment-fee-schedule']);
        }
        return paidStatus;
      }),
      take(1),
    );
  }
}

