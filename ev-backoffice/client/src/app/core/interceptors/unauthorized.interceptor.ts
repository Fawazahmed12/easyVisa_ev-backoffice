import {Injectable} from '@angular/core';
import {HttpErrorResponse, HttpHandler, HttpInterceptor, HttpRequest} from '@angular/common/http';

import {Store} from '@ngrx/store';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

import {catchError, switchMap, take} from 'rxjs/operators';
import {EMPTY, Observable, throwError} from 'rxjs';

import {State} from '../ngrx/state';
import {User} from '../models/user.model';
import {AuthService, ModalService, UserService} from '../services';

@Injectable()
export class UnauthorizedInterceptor implements HttpInterceptor {
  currentUser$: Observable<User>;

  constructor(
    private store: Store<State>,
    private modalService: ModalService,
    private ngbModal: NgbModal,
    private userService: UserService,
    private authService: AuthService,
  ) {
  }

  intercept(req: HttpRequest<any>, next: HttpHandler) {
    this.currentUser$ = this.userService.currentUser$;

    return this.currentUser$.pipe(
      take(1),
      switchMap((currentUser) => next.handle(req)
          .pipe(
            catchError(err => {
              if (err instanceof HttpErrorResponse && err.status === 401 && req.url !== '/login' && currentUser) {
                this.authService.removeAllCookies();
                window.location.reload();
                return EMPTY;
              }
              return throwError(err);
            })
          )),
    );
  }
}
