import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { filter, switchMap, take, withLatestFrom } from 'rxjs/operators';

import { OrganizationService } from '../../core/services';

import { WarningsService } from './warnings.service';

@Injectable()
export class WarningsResolverService implements Resolve<any> {

  constructor(
    private organizationService: OrganizationService,
    private warningsService: WarningsService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.currentRepresentativeId$.pipe(
      filter(val => val !== undefined),
      withLatestFrom(this.organizationService.activeOrganizationId$),
      switchMap(([representativeId, organizationId]) => {
        const params = {
          sort: route.queryParams.sort || 'date',
          order: route.queryParams.order || 'desc',
          representativeId: route.queryParams.representativeId || representativeId,
          organizationId: route.queryParams.organizationId || organizationId,
        };
        return this.warningsService.getWarnings(params);
      }),
      take(1)
    );
  }
}
