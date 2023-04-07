import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';

import { catchError, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { DashboardSettingsService } from '../settings.service';

@Injectable()
export class BatchJobsResolverService implements Resolve<any> {

  constructor(
    public dashboardSettingsService: DashboardSettingsService,
  ) {
  }

  resolve() {
    return this.dashboardSettingsService.getBatchJob().pipe(
      catchError(() => of(true)),
      take(1)
    );
  }
}
