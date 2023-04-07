import { Component, OnInit } from '@angular/core';

import { combineLatest, Observable } from 'rxjs';
import { filter, map, startWith } from 'rxjs/operators';

import { OrganizationService, UserService } from '../core/services';
import { Role } from '../core/models/role.enum';
import { Tab } from '../core/models/tab.model';
import { rolesHasAccess } from '../shared/utils/roles-has-access';
import { User } from '../core/models/user.model';
import { Attorney } from '../core/models/attorney.model';

import { EmployeePosition } from './permissions/models/employee-position.enum';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss'],
})
export class AccountComponent implements OnInit {
  tabs$: Observable<Tab[]>;

  constructor(
    private userService: UserService,
    private organizationService: OrganizationService,
  ) {
  }

  ngOnInit() {
    this.tabs$ = combineLatest([
      this.organizationService.currentPosition$,
      this.organizationService.isAdmin$.pipe(
        startWith<any, any>(null)
      ),
      this.userService.activeMembership$,
      this.userService.paidStatus$,
      this.userService.currentUser$.pipe(
        filter((user) => !!user)
      ),
      this.organizationService.withoutOrganizations$,
    ]).pipe(
      map((
        [
          currentPosition,
          isAdmin,
          activeMembership,
          paidStatus,
          user,
          withoutOrganizations
        ]: [
          EmployeePosition,
          boolean,
          boolean,
          boolean,
          User,
          boolean
        ]) => {
          const isClient = !!user.roles.find((role) => role === Role.ROLE_USER);
          const isTrainee = currentPosition === EmployeePosition.TRAINEE;
          const isEmployee = currentPosition === EmployeePosition.EMPLOYEE;
          const forRepresentative = [
            {
              title: 'TEMPLATE.ACCOUNT.NAV.PROFILE',
              link: ['/account', 'profile'],
              disabled: !paidStatus
            },
            {
              title: 'TEMPLATE.ACCOUNT.NAV.PAYMENT_FEE_SCHEDULE',
              link: ['/account', 'payment-fee-schedule'],
              disabled:
                !(isAdmin || rolesHasAccess(user.roles, [Role.ROLE_OWNER, Role.ROLE_ATTORNEY, Role.ROLE_EMPLOYEE]))
                || isTrainee
                || (isEmployee && !isAdmin)
                || withoutOrganizations,
              showExclamation: user && -(user.profile as Attorney).balance < 0,
            },
            {
              title: 'TEMPLATE.ACCOUNT.NAV.NOTIFICATIONS_REMINDERS',
              link: ['/account', 'notifications-reminders'],
              disabled:
                !(isAdmin || rolesHasAccess(user.roles, [Role.ROLE_OWNER, Role.ROLE_ATTORNEY, Role.ROLE_EMPLOYEE]))
                || !activeMembership
                || !paidStatus
                || isTrainee
                || (isEmployee && !isAdmin)
                || withoutOrganizations
            },
            {
              title: 'TEMPLATE.ACCOUNT.NAV.EMAIL_TEMPLATES',
              link: ['/account', 'email-templates'],
              disabled:
                !activeMembership
                || !paidStatus
                || isTrainee
                || (isEmployee && !isAdmin)
                || withoutOrganizations
            },
            {
              title: 'TEMPLATE.ACCOUNT.NAV.PERMISSIONS',
              link: ['/account', 'permissions'],
              disabled: !activeMembership || !paidStatus || withoutOrganizations
            }
          ];
          const forClient = [
            {
              title: 'TEMPLATE.ACCOUNT.NAV.PROFILE',
              link: ['/account', 'profile'],
            },
            {
              title: 'TEMPLATE.ACCOUNT.NAV.WRITE_A_REVIEW',
              link: ['/account', 'write-a-review'],
            },
            {
              title: 'TEMPLATE.ACCOUNT.NAV.LEGAL_REPRESENTATIVE',
              link: ['/account', 'legal-representative'],
            },
          ];
          return isClient ? forClient : forRepresentative;
        }
      )
    );
  }
}
