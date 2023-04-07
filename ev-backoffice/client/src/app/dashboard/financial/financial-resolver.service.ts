import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, switchMap, take, withLatestFrom } from 'rxjs/operators';
import { of } from 'rxjs';

import { OrganizationService, UserService } from '../../core/services';

import { FinancialService } from './financial.service';


@Injectable()
export class FinancialResolverService implements Resolve<any> {

  constructor(
    private organizationService: OrganizationService,
    private financialService: FinancialService,
    private userService: UserService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.currentRepIdOrgId$.pipe(
      withLatestFrom(
        this.organizationService.isAdmin$,
        this.userService.isCurrentRepresentativeMe$
      ),
      switchMap(([[representativeId, activeOrganizationId], isAdmin, isMe]) => {
        const params = {
          organizationId: route.queryParams.organizationId || activeOrganizationId,
          representativeId: route.queryParams.representativeId || representativeId,
        };
        if ((!isAdmin && !isMe) || !params.representativeId) {
          return of(true);
        }
        return this.financialService.getFinancialDetails(params).pipe(
          catchError(() => of(true))
        );
      }),
      take(1)
    );
  }
}
