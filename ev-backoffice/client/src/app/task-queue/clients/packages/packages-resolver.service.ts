import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { filter, switchMap, take, withLatestFrom } from 'rxjs/operators';

import { OrganizationService, PackagesService } from '../../../core/services';

@Injectable()
export class PackagesResolverService implements Resolve<any> {

  constructor(
    private packagesService: PackagesService,
    private organizationService: OrganizationService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.currentRepresentativeId$.pipe(
      filter(val => val !== undefined),
      withLatestFrom(
        this.organizationService.activeOrganizationId$.pipe(
        filter((id) => !!id)),
      ),
      switchMap(([representativeId, organizationId]) => {
        const params = {
          sort: route.queryParams.sort || 'status',
          benefitCategory: route.queryParams.benefitCategory || null,
          countries: route.queryParams.countries || null,
          closedDateStart: route.queryParams.closedDateStart || null,
          closedDateEnd: route.queryParams.closedDateEnd || null,
          openedDateStart: route.queryParams.openedDateStart || null,
          openedDateEnd: route.queryParams.openedDateEnd || null,
          lastAnsweredOnDateStart: route.queryParams.lastAnsweredOnDateStart || null,
          lastAnsweredOnDateEnd: route.queryParams.lastAnsweredOnDateEnd || null,
          easyVisaId: route.queryParams.easyVisaId || null,
          isOwed: route.queryParams.isOwed || null,
          lastName: route.queryParams.lastName || null,
          max: route.queryParams.max || 25,
          mobileNumber: route.queryParams.mobileNumber || null,
          offset: route.queryParams.offset || 0,
          order: route.queryParams.order || 'asc',
          petitionerStatus: route.queryParams.petitionerStatus || null,
          representativeId: parseInt(route.queryParams.representativeId, 10) || representativeId,
          status: route.queryParams.status || null,
          states: route.queryParams.states || null,
          organizationId: parseInt(route.queryParams.organizationId, 10) || organizationId,
        };
        return this.packagesService.getPackages(params);
      }),
      take(1)
    );
  }
}
