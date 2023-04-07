import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { UserService } from '../services';
import { PaymentService } from '../services';


@Injectable()
export class MyBalanceResolverService implements Resolve<any> {

  constructor(
    private paymentService: PaymentService,
    private userService: UserService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.userService.currentUser$.pipe(
      filter((res) => !!res),
      switchMap((user) => this.paymentService.getMyBalance(user.id)),
      catchError((err) => of(true)),
      take(1),
    );
  }
}
