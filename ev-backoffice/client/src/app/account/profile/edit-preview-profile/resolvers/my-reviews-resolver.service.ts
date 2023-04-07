import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, map, switchMap, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { ReviewService } from '../../../services/review.service';
import { Role } from '../../../../core/models/role.enum';
import { UserService } from '../../../../core/services';


@Injectable()
export class MyReviewsResolverService implements Resolve<any> {

  constructor(
    private reviewService: ReviewService,
    private userService: UserService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const params = {
      rating: route.queryParams.rating,
      sort: route.queryParams.sort || 'date',
      order: route.queryParams.order || 'desc',
      offset: route.queryParams.offset || 0,
    };

    return this.userService.currentUser$.pipe(
      map((user) => {
          const isAttorney = user.roles.some((role: Role) => role === Role.ROLE_ATTORNEY);
          return [isAttorney, user.activeMembership];
        }
      ),
      switchMap(([isAttorney, activeMembership]) => {
          if (isAttorney && activeMembership) {
            return this.reviewService.getReviews(params).pipe(
              catchError(() => of(true)),
            );
          } else {
            return of(true);
          }
        }
      ),
      take(1)
    );
  }
}
