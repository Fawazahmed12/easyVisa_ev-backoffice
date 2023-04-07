import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { EMPTY, of } from 'rxjs';

import { PaymentService, UserService } from '../services';
import { Role } from '../models/role.enum';


@Injectable()
export class MyAccountTransactionsResolverService implements Resolve<any> {

  constructor(
    private paymentService: PaymentService,
    private userService: UserService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const params = {
      max: route.queryParams.max || 25,
      offset: route.queryParams.offset || 0,
    };

    return this.userService.currentUser$.pipe(
      filter((user) => !!user),
      switchMap((user) => {
          const isValidRole = !user.roles.includes(Role.ROLE_EMPLOYEE);
          return isValidRole ? this.paymentService.getAccountTransactions({
              id: user.id,
              params
            })
            : of(true);
        }
      ),
      catchError(() => EMPTY),
      take(1),
    );
  }
}
