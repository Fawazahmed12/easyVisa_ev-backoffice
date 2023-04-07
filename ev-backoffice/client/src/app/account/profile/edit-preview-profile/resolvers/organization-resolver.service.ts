import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, switchMap, take, withLatestFrom } from 'rxjs/operators';
import { of } from 'rxjs';

import { OrganizationService, UserService } from '../../../../core/services';

import { EditPreviewProfileService } from '../edit-preview-profile.service';

@Injectable()
export class OrganizationResolverService implements Resolve<any> {

  constructor(
    private organizationService: OrganizationService,
    private userService: UserService,
    private profileService: EditPreviewProfileService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.organizationService.activeOrganizationId$.pipe(
      withLatestFrom(this.userService.activeMembership$),
      switchMap(([id, activeMembership]) => {
        if (id && activeMembership) {
          return this.profileService.getOrganization(id).pipe(
            catchError((err) => of(true))
          );
        } else {
          return of(true);
        }
      }),
      take(1),
    );
  }
}
