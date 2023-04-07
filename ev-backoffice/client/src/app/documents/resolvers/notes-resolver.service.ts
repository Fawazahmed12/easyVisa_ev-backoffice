import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';

import { EMPTY } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';

import { PackagesService } from '../../core/services';
import { RepresentativeNotesService } from '../services/representative-notes.service';

@Injectable()
export class NotesResolverService implements Resolve<any> {

  constructor(
    private httpClient: HttpClient,
    private packagesService: PackagesService,
    private router: Router,
    private representativeNotesService: RepresentativeNotesService,
  ) {
  }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    return this.packagesService.activePackageId$
      .pipe(
        filter((activePackageId) => !!activePackageId),
        switchMap((activePackageId) => this.representativeNotesService.getNotes({ packageId: activePackageId })),
        take(1),
        catchError(() => EMPTY)
      );
  }
}
