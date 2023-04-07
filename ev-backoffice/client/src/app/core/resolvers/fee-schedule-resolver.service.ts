import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, filter, map, pluck, switchMap, take, withLatestFrom } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { of } from 'rxjs';

import { EmailTemplatesService, OrganizationService, UserService } from '../services';
import { RequestState } from '../ngrx/utils';
import { AttorneyMenu } from '../models/attorney.model';
import { EmployeePosition } from "../../account/permissions/models/employee-position.enum";


@Injectable()
export class FeeScheduleResolverService implements Resolve<any> {

  private currentRepresentativeId$: Observable<number> = this.organizationService.currentRepresentativeId$;
  private isAdmin$: Observable<boolean> = this.organizationService.isAdmin$;
  private currentRepresentativeFeeScheduleGetRequest$: Observable<RequestState<any>>
    = this.organizationService.currentRepresentativeFeeScheduleGetRequest$;

  constructor(
    private emailTemplatesService: EmailTemplatesService,
    private organizationService: OrganizationService,
    private userService: UserService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.representativesMenuRequestState$.pipe(
      filter((request) => !request.loading && !!request.data),
      pluck('data'),
      withLatestFrom(
        this.currentRepresentativeId$,
        this.currentRepresentativeFeeScheduleGetRequest$.pipe(map((res) => res.data)),
        this.isAdmin$,
        this.userService.currentUser$.pipe(pluck('id')),
        this.organizationService.currentPosition$
      ),
        switchMap(([repMenu, id, dataRes, isAdmin, currentUserId, currentPosition]: [AttorneyMenu[], number, any, boolean, string, EmployeePosition] ) => {
          const isInactive = dataRes && dataRes.error ? !!dataRes.error.errors.find((error) => error.errorCode === 'INACTIVE') : false;
          const isUnpaid = dataRes && dataRes.error ? !!dataRes.error.errors.find((error) => error.errorCode === 'UNPAID') : false;
          const hasAccessByPosition = [ EmployeePosition.PARTNER, EmployeePosition.ATTORNEY, EmployeePosition.MANAGER].includes(currentPosition)
          if (isInactive || isUnpaid) {
            return of(true);
          } else {
            const repId = isAdmin || hasAccessByPosition ? id : repMenu.find(rep => rep.userId === +currentUserId).id;
            return this.organizationService.getFeeSchedule(repId).pipe(catchError(() => of(true)));
          }
        }),
        take(1),
      );
  }
}
