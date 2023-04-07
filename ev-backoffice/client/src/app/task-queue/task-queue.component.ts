import { Component, OnInit } from '@angular/core';

import { combineLatest, Observable } from 'rxjs';
import { filter, map, startWith, switchMap } from 'rxjs/operators';

import { NotificationsService, OrganizationService, PackagesService } from '../core/services';
import { TaskQueueCounts } from '../core/models/task-queue-counts.model';
import { Tab } from '../core/models/tab.model';
import { EmployeePosition } from '../account/permissions/models/employee-position.enum';
import { Package } from '../core/models/package/package.model';
import { DispositionsService } from './dispositions/dispositions.service';
import { PackageStatus } from '../core/models/package/package-status.enum';

@Component({
  selector: 'app-task-queue',
  templateUrl: './task-queue.component.html',
  styleUrls: ['./task-queue.component.scss'],
})
export class TaskQueueComponent implements OnInit {
  taskQueueNotifications$: Observable<TaskQueueCounts>;
  currentPosition$: Observable<EmployeePosition>;
  activeOrganizationId$: Observable<string>;
  packagesTotalCount$: Observable<number>;
  packages$: Observable<Package[]>;
  totalDispositions$: Observable<string>;

  tabs$: Observable<Tab[]>;

  constructor(
    private notificationsService: NotificationsService,
    private organizationService: OrganizationService,
    private packagesService: PackagesService,
    private dispositionsService: DispositionsService
  ) {
  }

  ngOnInit() {
    this.taskQueueNotifications$ = this.notificationsService.taskQueueNotifications$.pipe(
      filter((notifications) => !!notifications)
    );
    this.currentPosition$ = this.organizationService.currentPosition$;
    this.packagesTotalCount$ = this.packagesService.total$;
    this.packages$ = this.packagesService.packages$;
    this.totalDispositions$ = this.dispositionsService.totalDispositions$;

    this.tabs$ = combineLatest([
      this.taskQueueNotifications$.pipe(startWith(null)),
      this.packagesTotalCount$,
      this.packages$,
      this.organizationService.withoutOrganizations$,
      this.packagesService.activePackage$,
    ]).pipe(
      switchMap((
        [
          notifications,
          total,
          packages,
          noOrganizationEmployee,
          activePackage
        ]: [
          TaskQueueCounts,
          number,
          Package[],
          boolean,
          Package
        ]) => this.currentPosition$.pipe(
        map((currentPosition) => [notifications, currentPosition, total, packages,
          noOrganizationEmployee, activePackage])
      )),
      map((
        [
          notifications,
          currentPosition,
          total,
          packages,
          noOrganizationEmployee,
          activePackage
        ]: [
          TaskQueueCounts,
          EmployeePosition,
          number,
          Package[],
          boolean,
          Package
        ]) => {
          const isTrainee = currentPosition === EmployeePosition.TRAINEE;
          const isTransferredPackage = activePackage && activePackage.status === PackageStatus.TRANSFERRED;
          const value = total ? `${packages.length}/${total}` : '';
          if (noOrganizationEmployee) {
            return [
              {
                title: 'TEMPLATE.TASK_QUEUE.NAV.ALERTS',
                link: ['/task-queue', 'alerts'],
              }
            ];
          } else {
            return [
              {
                title: 'TEMPLATE.TASK_QUEUE.NAV.DISPOSITIONS',
                link: ['/task-queue', 'dispositions'],
                count: !!notifications ? notifications.dispositionsCount : null,
              },
              {
                title: 'TEMPLATE.TASK_QUEUE.NAV.ALERTS',
                link: ['/task-queue', 'alerts'],
                count: !!notifications ? notifications.alerts.unread : null,
              },
              {
                title: 'TEMPLATE.TASK_QUEUE.NAV.WARNINGS',
                link: ['/task-queue', 'warnings'],
                count: !!notifications ? notifications.warnings.unread : null,
              },
              {
                title: 'TEMPLATE.TASK_QUEUE.NAV.CLIENTS',
                link: ['/task-queue', 'clients'],
                packagesCount: value,
                class: 'clients-tab'
              },
              {
                title: 'TEMPLATE.TASK_QUEUE.NAV.CREATE_EDIT_PACKAGE',
                link: ['/task-queue', 'package'],
                disabled: isTrainee
              },
              {
                title: 'TEMPLATE.TASK_QUEUE.NAV.ADDITIONAL_FEES',
                link: ['/task-queue', 'additional-fees'],
                disabled: isTransferredPackage || isTrainee
              },
            ];
          }
        }
      )
    );
  }
}
