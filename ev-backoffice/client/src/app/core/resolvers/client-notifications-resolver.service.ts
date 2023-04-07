import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { switchMap, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { NotificationsService, UserService } from '../services';
import { Role } from '../models/role.enum';

@Injectable()
export class ClientNotificationsResolverService implements Resolve<any> {

  constructor(
    private notificationsService: NotificationsService,
    private userService: UserService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.userService.hasAccess([Role.ROLE_USER]).pipe(
      switchMap((isUser: boolean) => {
          if (!isUser) {
            return of(true);
          } else {
            return this.notificationsService.getTaskQueueNotifications(null);
          }
        }
      ),
      take(1)
    );
  }
}
