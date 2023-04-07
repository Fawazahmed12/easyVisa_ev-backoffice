import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { take } from 'rxjs/operators';

import { AlertsService } from '../services/alerts.service';

@Injectable()
export class AlertsResolverService implements Resolve<any> {

  constructor(
    private alertsService: AlertsService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const params = {
      sort: route.queryParams.sort || 'date',
      order: route.queryParams.order || 'desc',
    };
    return this.alertsService.getAlerts(params).pipe(
      take(1)
    );
  }
}
