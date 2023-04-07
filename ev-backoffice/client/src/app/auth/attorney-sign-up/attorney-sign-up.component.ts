import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { EMPTY, Observable, of, Subject } from 'rxjs';
import { catchError, switchMap, take } from 'rxjs/operators';

import { AddSubscribers, DestroySubscribers } from 'ngx-destroy-subscribers';

import { I18nService } from '../../core/i18n/i18n.service';

import { AsyncRequestValidator } from '../../shared/validators/async-request.validator';
import { OkButtonLg } from '../../core/modals/confirm-modal/confirm-modal.component';
import { Attorney } from '../../core/models/attorney.model';
import { RequestState } from '../../core/ngrx/utils';
import { ModalService } from '../../core/services';

import { SignUpService } from '../services';
import { strengthPasswordValidator } from '../validators/strength-password.validator';
import { noWhitespaceValidator } from '../validators/no-white-space.validator';
import { compareValueValidator } from '../validators/compare-value.validator';

@Component({
  selector: 'app-attorney-sign-up',
  templateUrl: './attorney-sign-up.component.html',
  styleUrls: [ './attorney-sign-up.component.scss' ]
})
@DestroySubscribers()
export class AttorneySignUpComponent implements OnInit, OnDestroy, AddSubscribers {
  formGroup: FormGroup;
  attorneyRequest$: Observable<RequestState<Attorney>> = this.signUpService.attorneyPostState$;
  private createAttorneySubject$ = new Subject<Attorney>();
  private createAttorneyRequest$: Observable<any>;
  private attorneySignUpInfo$: Observable<Attorney> = this.signUpService.attorneySignUpInfo$;
  private subscribers: any = {};

  constructor(
    private signUpService: SignUpService,
    private modalService: ModalService,
    private i18nService: I18nService,
    private router: Router,
  ) {
    this.createFormGroup();
  }

  ngOnInit() {
    this.createAttorneyRequest$ = this.createAttorneySubject$.pipe(
      switchMap((data) => this.formGroup.valid ?
          this.signUpService.createAttorney(data).pipe(catchError(() => EMPTY))
          : this.openFormInvalidModal())
    );
  }

  addSubscribers() {
    this.subscribers.createAttorneySubscribtion = this.createAttorneyRequest$
      .subscribe((data) => {
        if (this.formGroup.valid) {
          this.router.navigate([ 'auth', 'sign-up-success' ]);
        }
      });
    this.subscribers.signUpInfoSubscription = this.attorneySignUpInfo$.pipe(
      take(1),
    )
      .subscribe((info) => {
        this.createFormGroup(info);
      });
    this.subscribers.currentLangSubscription = this.i18nService.currentLang$
      .subscribe((lang) =>
        this.formGroup.patchValue({ language: lang })
      );
  }

  ngOnDestroy() {
    console.log(`${this.constructor.name} Destroys`);
  }

  createFormGroup(data?) {
    this.formGroup = new FormGroup({
      firstName: new FormControl(
        { value: data ? data.firstName : '', disabled: data && data.easyVisaId },
        {
          updateOn: 'change',
          validators: [ Validators.required, noWhitespaceValidator ]
        }
      ),
      middleName: new FormControl(
        { value: data ? data.middleName : '', disabled: data && data.easyVisaId }
      ),
      lastName: new FormControl(
        { value: data ? data.lastName : '', disabled: data && data.easyVisaId },
        {
          updateOn: 'change',
          validators: [ Validators.required, noWhitespaceValidator ]
        }
      ),
      email: new FormControl(data ? data.email : '',
        {
          updateOn: 'change',
          validators: [
            Validators.required,
            Validators.email,
          ],
          asyncValidators: AsyncRequestValidator.createValidator(
            (value) => this.signUpService.emailValidateRequest(value),
            data ? data.email : '',
          ),
        },
      ),
      username: new FormControl(
        {
          value: data ? data.username : '',
          disabled: data && data.easyVisaId,
        },
        {
          updateOn: 'blur',
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
        {
          updateOn: 'change',
          validators: [ Validators.required ]
        }
      ),
    }, {
      updateOn: 'submit',
      validators: [
        compareValueValidator('password', 'repeatPassword'),
      ],
    });
  }

  formSubmit() {
    this.createAttorneySubject$.next(this.formGroup.getRawValue());
  }

  openFormInvalidModal() {
    return this.modalService.openConfirmModal({
      header: 'TEMPLATE.AUTH.SIGN_UP.FORM_INVALID',
      body: 'TEMPLATE.AUTH.SIGN_UP.FORM_INVALID_DESC',
      buttons: [ OkButtonLg ],
      centered: true,
    }).pipe(
      catchError(err => of(true))
    );
  }

  goRegistrationPage() {
    this.router.navigate(
      [ 'auth', 'registration' ],
      {
        queryParamsHandling: 'merge',
      }
    );
  }

}
