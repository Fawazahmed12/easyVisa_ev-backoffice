import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Router } from '@angular/router';

import { Store } from '@ngrx/store';
import { catchError, switchMap, take } from 'rxjs/operators';
import { Observable, throwError } from 'rxjs';

import { State } from '../ngrx/state';
import { User } from '../models/user.model';
import { ModalService, UserService } from '../services';

@Injectable()
export class UnpaidInterceptor implements HttpInterceptor {
  currentUser$: Observable<User>;

  constructor(
    private store: Store<State>,
    private modalService: ModalService,
    private userService: UserService,
    private router: Router,
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    this.currentUser$ = this.userService.currentUser$;

    return this.currentUser$.pipe(
      take(1),
      switchMap((currentUser: User) => next.handle(req)
        .pipe(
          catchError((err) => {
            const isInactive = err.error.errors && !!err.error.errors.find((error) => error.errorCode === 'INACTIVE');
            const isUnpaid = err.error.errors && !!err.error.errors.find((error) => error.errorCode === 'UNPAID');

            if (err instanceof HttpErrorResponse && err.status === 402 && isInactive) {
              if (currentUser.activeMembership) {
                this.userService.cancelMembership({
                  ...currentUser,
                  activeMembership: false
                });
              }
              this.router.navigate(['/account', 'profile']);
            } else if (err instanceof HttpErrorResponse && err.status === 402 && isUnpaid) {
              if (currentUser.paid) {
                this.userService.changeUser({
                  ...currentUser,
                  paid: false
                });
              }
              this.router.navigate(['/account', 'payment-fee-schedule']);
            }
            return throwError(err);
          })
        )),
    );
  }
}
