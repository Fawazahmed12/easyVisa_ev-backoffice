import { Component, OnInit } from '@angular/core';

import { filter, map, startWith } from 'rxjs/operators';
import { combineLatest, Observable } from 'rxjs';

import { NotificationsService, OrganizationService, UserService } from '../core/services';
import { Role } from '../core/models/role.enum';
import { EmployeePosition } from '../account/permissions/models/employee-position.enum';
import { Tab } from '../core/models/tab.model';
import { TaskQueueCounts } from '../core/models/task-queue-counts.model';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {

  tabs$: Observable<Tab[]>;
  taskQueueNotifications$: Observable<TaskQueueCounts>;


  constructor(
    private userService: UserService,
    private organizationService: OrganizationService,
    private notificationsService: NotificationsService,
  ) {
  }

  ngOnInit() {
    this.taskQueueNotifications$ = this.notificationsService.taskQueueNotifications$.pipe(
      filter((notifications) => !!notifications)
    );
    this.tabs$ = combineLatest([
      this.taskQueueNotifications$.pipe(startWith(null)),
      this.organizationService.currentPosition$,
      this.userService.currentUserRoles$,
      this.organizationService.isAdmin$,
    ]).pipe(
      map(([notifications, currentPosition, roles, isAdmin]: [TaskQueueCounts, EmployeePosition, Role[], Boolean]) => {
          const isClient = !!roles.find((role) => role === Role.ROLE_USER);
          const isTrainee = currentPosition === EmployeePosition.TRAINEE;
          const isEmployee = currentPosition === EmployeePosition.EMPLOYEE;
          const isEv = roles.includes(Role.ROLE_OWNER);
          const isManagerOnly = currentPosition === EmployeePosition.MANAGER && !isAdmin;
          const forRepresentative = [
            {
              title: 'TEMPLATE.DASHBOARD.NAV.FINANCIAL',
              link: ['/dashboard', 'financial'],
              disabled: isTrainee || isEmployee || isManagerOnly,
              class: 'custom-min-tab-width-110'
            },
            {
              title: 'TEMPLATE.DASHBOARD.NAV.MARKETING',
              link: ['/dashboard', 'marketing'],
              disabled: isTrainee || isEmployee || isManagerOnly,
              class: 'custom-min-tab-width-110'
            },
            {
              title: 'TEMPLATE.DASHBOARD.NAV.ARTICLES',
              link: ['/dashboard', 'articles'],
              class: 'custom-min-tab-width-110'
            },
            {
              title: 'TEMPLATE.DASHBOARD.NAV.PROCESS_LINKS',
              link: ['/dashboard', 'process-links'],
              class: 'custom-min-tab-width-110'
            },
            {
              title: 'TEMPLATE.DASHBOARD.NAV.TUTORIALS',
              link: ['/dashboard', 'tutorials'],
              class: 'custom-min-tab-width-110'
            },
            {
              title: 'TEMPLATE.DASHBOARD.NAV.SETTINGS',
              link: ['/dashboard', 'settings'],
              class: 'custom-min-tab-width-110',
              hide: !isEv
            },
            {
              title: 'TEMPLATE.DASHBOARD.NAV.USCIS_EDITION_DATES',
              link: ['/dashboard', 'uscis-edition-dates'],
              class: 'custom-min-tab-width-110'
            }
          ];

          const forClient = [
            {
              title: 'TEMPLATE.DASHBOARD.NAV.PROGRESS_STATUS',
              link: ['/dashboard', 'progress-status'],
              class: 'custom-min-tab-width-110'
            },
            {
              title: 'TEMPLATE.DASHBOARD.NAV.ALERTS',
              link: ['/dashboard', 'alerts'],
              count: !!notifications ? notifications.alerts.unread : null,
              class: 'custom-min-tab-width-110'
            },
            {
              title: 'TEMPLATE.DASHBOARD.NAV.PROCESS_LINKS',
              link: ['/dashboard', 'process-links'],
              class: 'custom-min-tab-width-110'
            },
            {
              title: 'TEMPLATE.DASHBOARD.NAV.TUTORIALS',
              link: ['/dashboard', 'tutorials'],
              class: 'custom-min-tab-width-110'
            },
          ];

          return isClient ? forClient : forRepresentative;
        }
      )
    );
  }
}
