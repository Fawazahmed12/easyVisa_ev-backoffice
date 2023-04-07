import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, switchMap, take } from 'rxjs/operators';

import { OrganizationService } from '../services';
import { of } from 'rxjs';

@Injectable()
export class AffiliatedOrganizationsResolverService implements Resolve<any> {

  constructor(
    private organizationService: OrganizationService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.organizations$.pipe(
      switchMap((organizations) => {
          if (!!organizations?.length) {
            return this.organizationService.getAffiliatedOrganizations().pipe(
              catchError(() => of(true)),
            );
          } else {
            return of(true);
          }
        }
      ),
      take(1)
    );
  }
}
