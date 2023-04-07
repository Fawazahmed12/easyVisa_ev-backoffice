import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, take } from 'rxjs/operators';

import { FeeScheduleService } from '../services';
import { of } from 'rxjs';

@Injectable()
export class FeeScheduleSettingsResolverService implements Resolve<any> {

  constructor(
    public feeScheduleService: FeeScheduleService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.feeScheduleService.getFeeScheduleSettings().pipe(
      catchError(() => of(true)),
      take(1)
    );
  }
}
