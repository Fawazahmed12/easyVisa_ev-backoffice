import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { take } from 'rxjs/operators';

import { ReferringUserService } from '../services';


@Injectable()
export class ReferringUserResolverService implements Resolve<any> {

  constructor(
    public referringUserService: ReferringUserService,
  ) {

  }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const referringToken = route.queryParams['ref_token'];
    if (referringToken) {
      return this.referringUserService.getReferringUser(referringToken).pipe(
        take(1)
      );
    } else {
      return null;
    }
  }
}
