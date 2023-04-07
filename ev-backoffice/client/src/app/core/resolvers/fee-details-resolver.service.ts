import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { take } from 'rxjs/operators';

import { ConfigDataService } from '../services';

@Injectable()
export class FeeDetailsResolverService implements Resolve<any> {

  constructor(
    public configDataService: ConfigDataService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.configDataService.getFeeDetails().pipe(
      take(1)
    );
  }
}
