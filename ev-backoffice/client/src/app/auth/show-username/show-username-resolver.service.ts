import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, take, tap } from 'rxjs/operators';
import { of } from 'rxjs';

import { ModalService } from '../../core/services';
import { ShowUsernameService } from './show-username.service';


@Injectable()
export class ShowUsernameResolverService implements Resolve<any> {

  constructor(
    public showUsernameService: ShowUsernameService,
    private router: Router,
    private modalService: ModalService,
  ) {

  }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const token = route.queryParams['token'];
    if (token) {
      return this.showUsernameService.getForgottenUsername(token).pipe(
        catchError((error: HttpErrorResponse) =>
          this.modalService.showErrorModal(error.error.errors).pipe(
            catchError((err) => of(err)),
            tap(() => this.router.navigate(['auth', 'login'])),
          )
        ),
        take(1)
      );
    } else {
      this.router.navigate(['auth', 'login']);
    }
  }
}
