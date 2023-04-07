import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { filter, startWith, switchMap, take } from 'rxjs/operators';
import { combineLatest, of } from 'rxjs';

import { NotificationsService, OrganizationService } from '../../core/services';
import { TaskQueueCounts } from '../../core/models/task-queue-counts.model';

@Injectable()
export class TaskQueueNotificationsResolverService implements Resolve<any> {

  constructor(
    private organizationService: OrganizationService,
    private notificationsService: NotificationsService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return combineLatest([
      this.organizationService.currentRepresentativeId$.pipe(
        filter(val => val !== undefined),
        startWith(null)
      ),
      this.organizationService.activeOrganizationId$.pipe(
        filter(id => !!id),
        startWith(null)
      ),
      this.notificationsService.taskQueueNotifications$.pipe(startWith(null)),
      this.organizationService.withoutOrganizations$,
    ]).pipe(
      switchMap(([representativeId, organizationId, taskQueueCounts, withoutOrganizations]: [number, string, TaskQueueCounts, boolean]) => {
          if (withoutOrganizations) {
            return of(true);
          } else {
            return !!taskQueueCounts ? of(true) : this.notificationsService.getTaskQueueNotifications(
              {
                representativeId,
                organizationId
              }
            );
          }
        }
      ),
      take(1)
    );
  }
}
