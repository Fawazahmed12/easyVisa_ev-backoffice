import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, filter, switchMap, take, withLatestFrom } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

import { OrganizationService } from '../../core/services';

import { DispositionsService } from './dispositions.service';


@Injectable()
export class DispositionsResolverService implements Resolve<any> {

  constructor(
    private organizationService: OrganizationService,
    private dispositionsService: DispositionsService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.currentRepresentativeId$.pipe(
      filter(val => val !== undefined),
      withLatestFrom(this.organizationService.activeOrganizationId$),
      switchMap(([representativeId, organizationId]) => {
        const params = {
          sort: route.queryParams.sort || 'star',
          order: route.queryParams.order || 'asc',
          representativeId: route.queryParams.representativeId || representativeId,
          organizationId: route.queryParams.organizationId || organizationId,
          };
          return this.dispositionsService.getDispositions(params);
        }
      ),
      take(1),
      catchError(() => EMPTY)
    );
  }
}
