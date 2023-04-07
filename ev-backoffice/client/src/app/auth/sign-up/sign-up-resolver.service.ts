import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Resolve, Router, RouterStateSnapshot } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { catchError, map, take } from 'rxjs/operators';
import { EMPTY } from 'rxjs';

import { ModalService } from '../../core/services';

import { SignUpService } from '../services';

@Injectable()
export class SignUpResolverService implements Resolve<any> {

  private RESCINDED = 'rescinded';

  constructor(
    private signUpService: SignUpService,
    private router: Router,
    private modalService: ModalService,
  ) {

  }
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    const token = route.queryParams['token'];
    const invitationStatus = route.queryParams['invitation_status'];
    if (token && !invitationStatus) {
      return this.signUpService.getSignUpData(token).pipe(
        catchError((error: HttpErrorResponse) =>
          this.modalService.showErrorModal(error.error.errors || [error.error]).pipe(
            catchError(() => EMPTY),
            map(() => this.router.navigate(['auth', 'login'])),
          )
        ),
        take(1)
      );
    } else if (!token && invitationStatus==this.RESCINDED) {
      // The applicant should not be allowed to register if they are not in the system.
      // This situation can happen, if the attorney invited an applicant, then deleted them.
      // So if user clicked on an invitation to register that is no longer valid, we could give an error message
      return this.modalService.showErrorModal('TEMPLATE.AUTH.SIGN_UP.INVITATION_RESCINDED').pipe(
        catchError(() => EMPTY),
        map(() => this.router.navigate(['auth', 'login'])),
      );
    } else {
      this.router.navigate(['auth', 'login']);
    }
  }
}
