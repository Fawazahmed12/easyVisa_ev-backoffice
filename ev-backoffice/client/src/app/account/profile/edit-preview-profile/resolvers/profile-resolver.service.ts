import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { catchError, take } from 'rxjs/operators';
import { of } from 'rxjs';

import { EditPreviewProfileService } from '../edit-preview-profile.service';

@Injectable()
export class ProfileResolverService implements Resolve<any> {

  constructor(
    private profileService: EditPreviewProfileService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.profileService.getProfile().pipe(
      catchError((err) => of(true)),
      take(1)
    );
  }
}
