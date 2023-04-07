import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';

import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { Store } from '@ngrx/store';
import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { AuthService, UserService, XsrfAppLoadService } from '../../services';
import { User } from '../../models/user.model';
import { LoginModel } from '../../models/login.model';
import { LogoutSuccess } from '../../ngrx/user/user.actions';
import { State } from '../../ngrx/state';

@DestroySubscribers()
@Component({
  selector: 'app-login-modal',
  templateUrl: 'login-modal.component.html',
})
export class LoginModalComponent implements OnInit, AddSubscribers, OnDestroy {
  currentUser$: Observable<User>;
  submitRequest$: Observable<any>;
  submitSubject$: Subject<LoginModel> = new Subject();

  loginInModalGroup: FormGroup;

  private subscribers: any = {};

  get usernameControl() {
    return this.loginInModalGroup.get('username');
  }

  get passwordControl() {
    return this.loginInModalGroup.get('password');
  }

  constructor(
    private activeModal: NgbActiveModal,
    private authService: AuthService,
    private router: Router,
    private store: Store<State>,
    private userService: UserService,
    private xsrfAppLoadService: XsrfAppLoadService,
  ) {
  }

  ngOnInit() {
    this.authService.preLoginInModal();
    this.currentUser$ = this.userService.currentUser$;
    this.loginInModalGroup = new FormGroup({
      username: new FormControl(null, Validators.required),
      password: new FormControl(null, Validators.required),
    });
  }

  addSubscribers() {
    this.subscribers.loginSubmitSubscription = this.submitSubject$.pipe(
      filter(() => this.loginInModalGroup.valid),
      take(1),
      switchMap(() => {
        const data = {
          username: this.usernameControl.value,
          password: this.passwordControl.value
        };
        return this.authService.loginInModal(data.username, data.password).pipe(
          take(1),
          catchError(() => EMPTY),
        );
      }),
    ).subscribe(() => this.modalClose());
  }

  onSubmit() {
    this.xsrfAppLoadService.initializeApp()
      .then((data) => {
        this.submitSubject$.next(this.loginInModalGroup.getRawValue());
      }, (error) => {
        console.error('Error occurred while creating XSRF');
      });
  }

  modalClose() {
    this.activeModal.close();
  }

  modalDismiss() {
    this.activeModal.close();
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  retrieveCredential() {
    this.modalDismiss();
    this.store.dispatch(new LogoutSuccess());
    this.router.navigate(['auth', 'retrieve-credential']);
  }

  logout() {
    this.modalDismiss();
    this.store.dispatch(new LogoutSuccess());
  }
}
