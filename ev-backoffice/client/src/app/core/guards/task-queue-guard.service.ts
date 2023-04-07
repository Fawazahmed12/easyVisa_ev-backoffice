import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';

import { Observable } from 'rxjs';
import { map, take } from 'rxjs/operators';

import { OrganizationService, UserService } from '../services';



@Injectable()
export class TaskQueueGuardService implements CanActivate {

  constructor(
    private router: Router,
    private userService: UserService,
    private organizationService: OrganizationService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    return this.organizationService.withoutOrganizations$.pipe(
      map(( withoutOrganizations) => {
        if (withoutOrganizations) {
          this.router.navigate(['task-queue', 'alerts']);
        } else {
          this.router.navigate(['task-queue', 'dispositions']);
        }
        return true;
      }),
      take(1),
    );
  }
}

