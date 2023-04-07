import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { DashboardSettingsService } from '../settings.service';

@Injectable()
export class RankingDataResolverService implements Resolve<any> {

  constructor(
    public dashboardSettingsService: DashboardSettingsService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.dashboardSettingsService.getRankingData().pipe(
      catchError(() => of(true)),
      take(1)
    );
  }
}
