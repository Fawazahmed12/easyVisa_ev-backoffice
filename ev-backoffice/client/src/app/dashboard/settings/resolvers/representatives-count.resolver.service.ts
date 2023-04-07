import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, map, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { DashboardSettingsService } from '../settings.service';

@Injectable()
export class RepresentativesCountResolverService implements Resolve<any> {

  constructor(
    public dashboardSettingsService: DashboardSettingsService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.dashboardSettingsService.getRepresentativesCount();
  }
}
