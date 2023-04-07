import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { Observable, of, Subject } from 'rxjs';
import { catchError, filter, share, switchMap } from 'rxjs/operators';

import { LoginModel } from '../../core/models/login.model';
import { AuthService, XsrfAppLoadService } from '../../core/services/';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: [ './login.component.scss' ]
})
@DestroySubscribers()
export class LoginComponent implements OnInit, OnDestroy, AddSubscribers {

  loginGroup: FormGroup;

  submit$ = new Subject<LoginModel>();
  public submitRequest$: Observable<any>;
  public loginRequest$: Observable<any> = this.authService.loginRequest$;
  isShowingError = false;
  private subscribers: any = {};

  constructor(
    public fb: FormBuilder,
    public router: Router,
    private authService: AuthService,
    private xsrfAppLoadService: XsrfAppLoadService
  ) {
  }

  ngOnInit() {
    this.loginGroup = this.fb.group({
      username: [ '', Validators.required ],
      password: [ '', Validators.required ]
    });
    this.submitRequest$ = this.submit$.pipe(
      filter(() => this.loginGroup.valid),
      switchMap(({ username, password }: LoginModel) =>
        this.authService.login(username, password)
          .pipe(
            catchError((error) => {
              if (error) {
                this.isShowingError = true;
              }
              return of({ loaded: false, loading: false, data: null, error });
            }),
          )
      ),
      share(),
    );
  }

  addSubscribers() {
    this.subscribers.submitSubsribtion = this.submitRequest$.subscribe();
    this.subscribers.loginGroupValueChangesSubsribtion = this.loginGroup.valueChanges.subscribe((data) => {
      if (this.isShowingError) {
        this.isShowingError = false;
      }
    });
  }

  ngOnDestroy() {
    this.isShowingError = false;
    console.log(`${this.constructor.name} Destroys`);
  }

  onSubmit(formData: LoginModel) {
    this.xsrfAppLoadService.loadXSRFIfNotExist().then((data) => {
      const userData = {
        username: formData.username.toLowerCase(),
        password: formData.password
      };
      this.submit$.next(userData);
    }, (error) => {
      console.error('Error occurred while creating XSRF');
      this.submitRequest$ = of({ loaded: false, loading: false, data: null, error });
    });
  }
}
