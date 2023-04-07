import { Injectable } from '@angular/core';
import {
  Router,
  CanActivate,
  ActivatedRouteSnapshot,
  RouterStateSnapshot,
} from '@angular/router';

import { Observable } from 'rxjs';
import { filter, map, startWith, take } from 'rxjs/operators';
import { combineLatest } from 'rxjs';

import { EmployeePosition } from '../../account/permissions/models/employee-position.enum';
import { OrganizationService, UserService } from '../services';
import { Role } from '../models/role.enum';


@Injectable()
export class DashboardGuardService implements CanActivate {

  constructor(
    private router: Router,
    private userService: UserService,
    private organizationService: OrganizationService,
  ) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | boolean {
    return combineLatest([
      this.userService.currentUserRoles$,
      this.organizationService.currentPosition$.pipe(startWith('null')),
    ]).pipe(
      filter(([user, currentPosition]) => !!user && !!currentPosition),
      take(1),
      map(([roles, position]) => {
        if (position === EmployeePosition.TRAINEE || position === EmployeePosition.EMPLOYEE) {
          this.router.navigate(['task-queue', 'alerts']);
        } else if (roles.some((role) => role !== Role.ROLE_USER)) {
          this.router.navigate(['dashboard', 'financial']);
        } else {
          this.router.navigate(['dashboard', 'progress-status']);
        } return true;
      }),
      take(1),
    );
  }
}
