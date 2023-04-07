import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormControl, FormGroup, Validators } from '@angular/forms';

import { Observable, Subject } from 'rxjs';
import { filter, map, share, switchMap } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { RequestState, ResponseStatus } from '../../core/ngrx/utils';

import { RetrieveCredentialService } from './retrieve-credential.service';

export interface CredentialValue {
  email: string;
  credential: string;
}

@Component({
  selector: 'app-retrieve-credential',
  templateUrl: './retrieve-credential.component.html',
  styleUrls: [ './retrieve-credential.component.scss' ]
})
@DestroySubscribers()
export class RetrieveCredentialComponent implements OnInit, OnDestroy, AddSubscribers {

  retrieveCredentialGroup: FormGroup;

  public forgotUsernameError$: Observable<any>;
  public forgotPasswordError$: Observable<any>;

  public forgotUsernameRequest$: Observable<RequestState<string>> = this.retrieveCredentialService.forgotUsernameRequest$;
  public forgotPasswordRequest$: Observable<RequestState<string>> = this.retrieveCredentialService.forgotPasswordRequest$;
  isShowingError = false;
  protected forgotUsernameResponse$: Observable<any> = new Observable<any>();
  protected forgotPasswordResponse$: Observable<any> = new Observable<any>();
  private forgotUsernameSubject$: Subject<CredentialValue> = new Subject<CredentialValue>();
  private forgotPasswordSubject$: Subject<CredentialValue> = new Subject<CredentialValue>();
  private subscribers: any = {};

  constructor(
    private retrieveCredentialService: RetrieveCredentialService,
    private router: Router,
  ) {
  }

  get emailControl() {
    return this.retrieveCredentialGroup.get('email');
  }

  get credentialControl() {
    return this.retrieveCredentialGroup.get('credential');
  }

  ngOnInit() {
    this.retrieveCredentialGroup = new FormGroup({
      credential: new FormControl('username',
        {
          updateOn: 'change',
          validators: [ Validators.required ]
        }),
      email: new FormControl('',
        {
          updateOn: 'change',
          validators: [
            Validators.required,
            Validators.email
          ]
        }),
    });

    this.forgotUsernameResponse$ = this.forgotUsernameSubject$.pipe(
      filter(() => this.retrieveCredentialGroup.valid),
      switchMap((value) =>
        this.retrieveCredentialService.forgotUsername(value)
      ),
      share()
    );

    this.forgotUsernameError$ = this.forgotUsernameResponse$.pipe(
      filter((res) => res.status === ResponseStatus.fail),
      map((res) => {
        if (res.data.error) {
          this.isShowingError = true;
        }
        return res.data.error.errors || [ res.data.error ];
      })
    );

    this.forgotPasswordResponse$ = this.forgotPasswordSubject$.pipe(
      filter(() => this.retrieveCredentialGroup.valid),
      switchMap((value) =>
        this.retrieveCredentialService.forgotPassword(value)
      ),
      share()
    );

    this.forgotPasswordError$ = this.forgotPasswordResponse$.pipe(
      filter((res) => res.status === ResponseStatus.fail),
      map((res) => {
        if (res.data.error) {
          this.isShowingError = true;
        }
        return res.data.error.errors || [ res.data.error ];
      })
    );

  }

  addSubscribers() {
    this.subscribers.forgotUsernameSubscription = this.forgotUsernameResponse$.pipe(
      filter((res) => res.status === ResponseStatus.success)
    )
      .subscribe(() =>
        this.router.navigate([ 'auth', 'retrieve-credential-success' ])
      );

    this.subscribers.forgotPasswordSubscription = this.forgotPasswordResponse$.pipe(
      filter((res) => res.status === ResponseStatus.success)
    )
      .subscribe(() =>
        this.router.navigate([ 'auth', 'retrieve-credential-success' ])
      );

    this.subscribers.retrieveCredentialGroupSubscription = this.retrieveCredentialGroup.valueChanges.subscribe((data) => {
      if (this.isShowingError) {
        this.isShowingError = false;
      }
    });
  }

  ngOnDestroy() {
    this.isShowingError = false;
    console.log(`${this.constructor.name} Destroys`);
  }

  onSubmit() {
    switch (this.credentialControl.value) {
      case 'username': {
        return this.forgotUsernameSubject$.next(this.retrieveCredentialGroup.value);
      }
      case 'password': {
        return this.forgotPasswordSubject$.next(this.retrieveCredentialGroup.value);
      }
    }
  }

}
