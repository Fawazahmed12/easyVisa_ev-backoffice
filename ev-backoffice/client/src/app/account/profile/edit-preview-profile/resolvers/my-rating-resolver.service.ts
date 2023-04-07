import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { UserService } from '../../../../core/services';
import { Role } from '../../../../core/models/role.enum';

import { ReviewService } from '../../../services/review.service';


@Injectable()
export class MyRatingsResolverService implements Resolve<any> {

  constructor(
    private reviewService: ReviewService,
    private userService: UserService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.userService.currentUser$.pipe(
      filter(user => !!user),
      switchMap(user => {
        const isUser = user.roles.includes(Role.ROLE_USER);
        if (isUser) {
          return of(true);
        } else {
          return this.reviewService.getRatings(user.profile.id).pipe(
            catchError(() => of(true)),
          );
        }
      }),
      take(1)
    );
  }
}
