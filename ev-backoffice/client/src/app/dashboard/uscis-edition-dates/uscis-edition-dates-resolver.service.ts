import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, RouterStateSnapshot } from '@angular/router';

import { filter, take } from 'rxjs/operators';

import { UscisEditionDatesService } from '../../core/services';


@Injectable()
export class UscisEditionDatesResolverService implements Resolve<any> {

  constructor(
    private uscisEditionDatesService: UscisEditionDatesService,
  ) {

  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.uscisEditionDatesService.getUscisEditionDates().pipe(
      filter((uscisEditionDates: any) => !!uscisEditionDates),
      take(1)
    );
  }
}
