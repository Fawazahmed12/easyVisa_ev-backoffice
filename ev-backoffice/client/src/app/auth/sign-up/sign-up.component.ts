import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

import { EMPTY, Observable, Subject } from 'rxjs';
import { catchError, filter, switchMap } from 'rxjs/operators';
import { fromPromise } from 'rxjs/internal-compatibility';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { AsyncRequestValidator } from '../../shared/validators/async-request.validator';
import { RequestState } from '../../core/ngrx/utils';
import { Profile } from '../../core/models/profile.model';
import { I18nService } from '../../core/i18n/i18n.service';
import { LoginResponse } from '../../core/models/login-response.model';
import { ModalService } from '../../core/services';

import { noWhitespaceValidator } from '../validators/no-white-space.validator';
import { compareValueValidator } from '../validators/compare-value.validator';
import { strengthPasswordValidator } from '../validators/strength-password.validator';
import { SignUpService } from '../services';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
})
@DestroySubscribers()
export class SignUpComponent implements OnInit, OnDestroy, AddSubscribers {
  signUpInfoGetRequest$: Observable<RequestState<Profile>>;
  userPostRequest$: Observable<RequestState<LoginResponse>>;
  formSubmitSubject$: Subject<boolean> = new Subject<boolean>();
  formGroup: FormGroup;

  private subscribers: any = {};

  constructor(
    private signUpService: SignUpService,
    private router: Router,
    private activatedRoute: ActivatedRoute,
    private i18nService: I18nService,
    private modalService: ModalService,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.signUpInfoGetRequest$ = this.signUpService.signUpInfoGetRequest$;
    this.userPostRequest$ = this.signUpService.userPostRequest$;
  }

  addSubscribers() {
    this.subscribers.signUpInfoSubscription = this.activatedRoute.queryParams.pipe(
      switchMap((params) => {
        this.formGroup.patchValue({token: params.token});
        return this.signUpInfoGetRequest$.pipe(
          filter((data) => data.loaded),
        );
      })
    )
    .subscribe((signUpInfo) => {
      this.formGroup.patchValue(signUpInfo.data);
    });

    this.subscribers.currentLangSubscription = this.i18nService.currentLang$
    .subscribe((lang) =>
      this.formGroup.patchValue({language: lang})
    );

    this.subscribers.formSubmitSubscription = this.formSubmitSubject$.pipe(
      filter(() => this.formGroup.valid),
      switchMap(() => this.signUpService.createUser(this.formGroup.value).pipe(
        catchError((error: HttpErrorResponse) =>
          this.modalService.showErrorModal(error.error.errors || [error.error]).pipe(
            catchError(() => EMPTY),
            switchMap(() => fromPromise(this.router.navigate(['auth', 'login']))),
          )
        ),
      )),
    )
    .subscribe();
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup(data?) {
    this.formGroup = new FormGroup({
      firstName: new FormControl(
        data ? data.firstName : '',
        [Validators.required, noWhitespaceValidator],
      ),
      middleName: new FormControl(data ? data.middleName : ''),
      lastName: new FormControl(
        data ? data.lastName : '',
        [Validators.required, noWhitespaceValidator],
      ),
      username: new FormControl(
        {
          value: data ? data.username : '',
          disabled: data && data.easyVisaId,
        },
        {
          updateOn: 'change',
          validators: [
            Validators.required,
          ],
          asyncValidators: AsyncRequestValidator.createValidator((value) => this.signUpService.usernameValidateRequest(value)),
        },
      ),
      language: new FormControl(''),
      password: new FormControl('', {
        updateOn: 'change',
        validators: [
          Validators.minLength(12),
          Validators.required,
          strengthPasswordValidator,
        ]
      }),
      repeatPassword: new FormControl('',
        [
          Validators.required,
        ],
      ),
      token: new FormControl(),
    }, {
      updateOn: 'submit',
      validators: [
        compareValueValidator('password', 'repeatPassword'),
      ],
    });
  }

  formSubmit() {
    this.formSubmitSubject$.next(true);
  }
}
