import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { filter, switchMap, take } from 'rxjs/operators';

import { OrganizationService } from '../../core/services';

import { PermissionsService } from './permissions.service';


@Injectable()
export class PermissionsResolverService implements Resolve<any> {

  constructor(
    private permissionsService: PermissionsService,
    private organizationService: OrganizationService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.activeOrganizationId$.pipe(
      filter((organizationId) => !!organizationId),
      switchMap((organizationId) => {
          const params = {
            sort: route.queryParams.sort || 'name',
            order: route.queryParams.order || 'asc',
            includeAll: route.queryParams.includeAll || false,
          };
          return this.permissionsService.getPermissions({params, organizationId});
        }
      ),
      take(1)
    );
  }
}
