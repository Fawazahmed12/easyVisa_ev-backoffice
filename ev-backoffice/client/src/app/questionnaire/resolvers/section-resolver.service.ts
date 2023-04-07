import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';

import { EMPTY } from 'rxjs';
import { catchError, switchMap, take } from 'rxjs/operators';

import { QuestionnaireService } from '../services/questionnaire.service';
import { PackagesService } from '../../core/services';

@Injectable()
export class SectionListResolverService implements Resolve<any> {

  constructor(
    private httpClient: HttpClient,
    private questionnaireService: QuestionnaireService,
    private packagesService: PackagesService,
    private router: Router
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.packagesService.activePackageId$
      .pipe(
        switchMap(activePackageId => this.questionnaireService.getSections(activePackageId)),
        take(1),
        catchError((err) => {
          this.router.navigate([ 'home' ]);
          return EMPTY;
        })
      );
  }
}
